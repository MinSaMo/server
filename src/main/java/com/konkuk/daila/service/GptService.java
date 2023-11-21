package com.konkuk.daila.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dao.member.Member;
import com.konkuk.daila.domain.dao.member.MemberTodolist;
import com.konkuk.daila.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.daila.domain.dto.response.DialogResponseDto;
import com.konkuk.daila.domain.dto.response.EmergencyCheckDto;
import com.konkuk.daila.domain.dto.response.IntenseResponseDto;
import com.konkuk.daila.domain.dto.response.TodoListResponseDto;
import com.konkuk.daila.global.logger.DashboardLogger;
import com.konkuk.daila.service.dialog.Message;
import com.konkuk.daila.service.enums.ChatType;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.github.flashvayne.chatgpt.property.ChatgptProperties;
import io.github.flashvayne.chatgpt.property.MultiChatProperties;
import io.github.flashvayne.chatgpt.service.ChatgptService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gpt.prompt.daily.param")
public class GptService {

    private final ChatgptService chatgptService;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final ChatgptProperties chatgptProperties;
    private final PromptManager promptManager;

    private final DashboardLogger logger;
    private final String SYSTEM_INTENSE = "intense";
    private final String SYSTEM_GLOBAL = "global";
    private final String SERVER_ERR_MSG = "Sorry, Server Error on Remote GPT. Please re-send message.";

    @Value("${gpt.system.intense}")
    private String intenseSystemScript;
    @Value("${gpt.system.global}")
    private String globalSystemScript;
    @Value("${gpt.prompt.daily.template}")
    private String dailyPromptTemplate;
    @Value("${gpt.system.information}")
    private String infoSystemScript;
    @Value("${gpt.system.check-todolist}")
    private String checkTodoListSystemScript;
    @Value("${gpt.system.check-emergency}")
    private String checkEmergencySystemScript;
    @Value("${gpt.system.llm}")
    private String llmSystemScript;
    @Value("${gpt.prompt.information-dump}")
    private String informationDumpScript;
    @Value("${gpt.api-key}")
    private String mainKey;
    @Value("${gpt.sub-key}")
    private String subKey;

    @Setter
    private String userInformation;
    @Setter
    private String message;


    public String chatProxy(List<MultiChatMessage> messages) {

        ExecutorService executor = Executors.newFixedThreadPool(1);
        String res = null;
        try {
            Future<String> future = executor.submit(() -> {
                return chatgptService.multiChat(messages);
            });

            res = future.get(60, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            res = null;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
        return res;
    }

    public String responseWithLLM(List<MultiChatMessage> messages) {
        String system = llmSystemScript;
        MultiChatMessage userMessage = messages.get(messages.size() - 1);
        userMessage.setContent(system + userMessage.getContent());
        try {
            chatgptProperties.setApiKey(subKey);
            String res = chatProxy(messages);
            if (res == null) return SERVER_ERR_MSG;
            DialogResponseDto obj = objectMapper.readValue(res, DialogResponseDto.class);
            chatgptProperties.setApiKey(mainKey);
            return obj.response();
        } catch (HttpServerErrorException e) {
            return SERVER_ERR_MSG;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public DialogResponseDto ask(String script, ChatType type, Long memberId, List<Message> currentDialog) {
        switch (type) {
            case DAILY, ADVICE -> {
                return responseWithDailyChat(script, memberId, currentDialog);
            }
            case EMERGENCY -> {
                return null;
            }
        }
        throw new RuntimeException();
    }

    public ChatType determineIntense(String script) {
        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        MultiChatMessage systemDefinition = getSystemDefinition(SYSTEM_INTENSE);
        List<MultiChatMessage> messages = Arrays.asList(systemDefinition, new MultiChatMessage("user", script));
        String response = chatProxy(messages);
        if (response == null) return ChatType.SERVER_ERR;

        multi.setTopP(originalTopP);
        multi.setTemperature(originalTemperature);
        try {
            IntenseResponseDto result = objectMapper.readValue(response, IntenseResponseDto.class);
            return ChatType.of(result.answerTypeIndex());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected DialogResponseDto responseWithDailyChat(String script, Long memberId, List<Message> currentDialog) {
//        MultiChatMessage systemDefinition = getSystemDefinition(SYSTEM_GLOBAL);
//
        Map<String, String> params = new HashMap<>();

        params.put(message, script);
        String information = memberService.getInformationString(memberId);
        params.put(userInformation, information);
//
//        String prompt = setPromptParams(dailyPromptTemplate, params);
//
//        List<Message> messages = new ArrayList<>();
//        messages.add(systemDefinition);
//        messages.addAll(currentDialog);
//        messages.add(new MultiChatMessage("user", prompt));
//        logger.sendPromptLog(prompt);
//
//        try {
//            String response = chatProxy(messages);
//            if (response == null) {
//                return new DialogResponseDto(SERVER_ERR_MSG);
//            }
//            return objectMapper.readValue(response, DialogResponseDto.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        } catch (HttpServerErrorException e) {
//            return new DialogResponseDto(SERVER_ERR_MSG);
//        }
        return null;
    }

    protected String setPromptParams(String template, Map<String, String> params) {
        String res = template;
        for (String name : params.keySet()) {
            res = res.replace(name, params.get(name));
        }
        return res;
    }

    public UserInformationGenerateDto generateUserInformation(List<Message> dialog, Long memberId) {

        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        ArrayList<Message> messages = new ArrayList<>(dialog);

        Map<String, String> params = new HashMap<>();
        params.put("$currentTime", LocalDateTime.now().toString());
        String informationString = memberService.getInformationString(memberId);
        String system = setPromptParams(infoSystemScript, params);
//        messages.add(new MultiChatMessage("system", system));
//        params = new HashMap<>();
//        params.put("$user-info", informationString);
//        String infoDump = setPromptParams(informationDumpScript, params);
//        messages.add(new MultiChatMessage("user", infoDump));

//        String response = chatgptService.multiChat(messages);

        return null;
//        multi.setTopP(originalTopP);
//        multi.setTemperature(originalTemperature);
//        try {
//            UserInformationGenerateDto res = objectMapper.readValue(response, UserInformationGenerateDto.class);
//            return res;
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Transactional
    public List<String> checkCompletedTodolist(String caption, Long memberId) {

        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        Member member = memberService.findMemberById(memberId);

        List<MultiChatMessage> messages = new ArrayList<>();
        messages.add(new MultiChatMessage("system", checkTodoListSystemScript));

        List<Todolist> list = member.getChecklistList()
                .stream().map(MemberTodolist::getChecklist).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("input : ")
                .append(caption)
                .append(",")
                .append("checklist : [");

        for (Todolist checklist : list) {
            sb.append(checklist.toString()).append(",");
        }
        sb.append("]");

        String prompt = sb.toString();
        messages.add(new MultiChatMessage("user", prompt));

        String response = chatgptService.multiChat(messages);
        List<Long> completeList = null;

        multi.setTopP(originalTopP);
        multi.setTemperature(originalTemperature);

        List<String> res = new ArrayList<>();
        try {
            completeList = objectMapper.readValue(response, TodoListResponseDto.class).complete();
            for (Long checklistId : completeList) {
                String checklistName = memberService.completeChecklist(checklistId, memberId);
                res.add(checklistName);
            }
            return res;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public EmergencyCheckDto checkEmergency(String caption) {
        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        List<MultiChatMessage> messages = new ArrayList<>();
        messages.add(new MultiChatMessage("system", checkEmergencySystemScript));
        String prompt = "\"input\" :\"" +
                caption
                + "\"";
        messages.add(new MultiChatMessage("user", prompt));

        log.info("[EMERGENCY] System Script : {}", checkTodoListSystemScript);
        log.info("[EMERGENCY] Prompt : {}", prompt);
        String response = chatgptService.multiChat(messages);
        log.info("[EMERGENCY] Result : {}", response);
        multi.setTopP(originalTopP);
        multi.setTemperature(originalTemperature);

        try {
            return objectMapper.readValue(response, EmergencyCheckDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected MultiChatMessage getSystemDefinition(String type) {
        switch (type) {
            case SYSTEM_INTENSE -> {
                return new MultiChatMessage("system", intenseSystemScript);
            }
            case SYSTEM_GLOBAL -> {
                return new MultiChatMessage("system", globalSystemScript);
            }
        }
        throw new RuntimeException();
    }

}
