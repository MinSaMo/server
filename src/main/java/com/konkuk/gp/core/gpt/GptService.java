package com.konkuk.gp.core.gpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konkuk.gp.core.gpt.enums.ChatType;
import com.konkuk.gp.domain.dao.Checklist;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.domain.dto.request.UserInformationGenerateDto;
import com.konkuk.gp.domain.dto.response.*;
import com.konkuk.gp.service.MemberService;
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

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "gpt.prompt.daily.param")
public class GptService {

    private final ChatgptService chatgptService;
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final ChatgptProperties chatgptProperties;

    private final String SYSTEM_INTENSE = "intense";
    private final String SYSTEM_GLOBAL = "global";

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

    @Setter
    private String userInformation;
    @Setter
    private String message;

    public DialogResponseDto ask(String script, ChatType type, Long memberId, List<MultiChatMessage> currentDialog) {
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
        MultiChatMessage systemDefinition = getSystemDefinition(SYSTEM_INTENSE);
        log.info("[INTENSE] System script : {}", systemDefinition);
        List<MultiChatMessage> messages = Arrays.asList(systemDefinition, new MultiChatMessage("user", script));
        log.info("[INTENSE] Prompt : {}", script);
        String response = chatgptService.multiChat(messages);
        log.info("[INTENSE] Response : {}",response);
        try {
            IntenseResponseDto result = objectMapper.readValue(response, IntenseResponseDto.class);
            return ChatType.of(result.answerTypeIndex());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected DialogResponseDto responseWithDailyChat(String script, Long memberId, List<MultiChatMessage> currentDialog) {
        MultiChatMessage systemDefinition = getSystemDefinition(SYSTEM_GLOBAL);

        Map<String, String> params = new HashMap<>();

        params.put(message, script);
        String information = memberService.getInformationString(memberId);
        params.put(userInformation, information);

        String prompt = setPromptParams(dailyPromptTemplate, params);

        List<MultiChatMessage> messages = new ArrayList<>();
        messages.add(systemDefinition);
        messages.addAll(currentDialog);
        messages.add(new MultiChatMessage("user", prompt));

        String response = chatgptService.multiChat(messages);
        try {
            return objectMapper.readValue(response, DialogResponseDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected String setPromptParams(String template, Map<String, String> params) {
        String res = template;
        for (String name : params.keySet()) {
            res = res.replace(name, params.get(name));
        }
        return res;
    }

    public UserInformationGenerateDto generateUserInformation(List<MultiChatMessage> dialog) {

        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        ArrayList<MultiChatMessage> messages = new ArrayList<>(dialog);

        Map<String, String> params = new HashMap<>();
        params.put("$currentTime", LocalDateTime.now().toString());
        String system = setPromptParams(infoSystemScript, params);

        messages.add(new MultiChatMessage("system", system));

        String response = chatgptService.multiChat(messages);

        multi.setTopP(originalTopP);
        multi.setTemperature(originalTemperature);
        try {
            UserInformationGenerateDto res = objectMapper.readValue(response, UserInformationGenerateDto.class);
            return res;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void checkCompletedTodolist(String caption, Long memberId) {

        MultiChatProperties multi = chatgptProperties.getMulti();

        double originalTopP = multi.getTopP();
        double originalTemperature = multi.getTemperature();

        multi.setTopP(0.3);
        multi.setTemperature(0.1);

        Member member = memberService.findMemberById(memberId);

        List<MultiChatMessage> messages = new ArrayList<>();
        messages.add(new MultiChatMessage("system", checkTodoListSystemScript));

        List<Checklist> list = member.getChecklistList()
                .stream().map(MemberChecklist::getChecklist).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("input : ")
                .append(caption)
                .append(",")
                .append("checklist : [");

        for (Checklist checklist : list) {
            sb.append(checklist.toString()).append(",");
        }
        sb.append("]");

        String prompt = sb.toString();
        messages.add(new MultiChatMessage("user", prompt));

        String response = chatgptService.multiChat(messages);
        List<Long> completeList = null;

        multi.setTopP(originalTopP);
        multi.setTemperature(originalTemperature);
        try {
            completeList = objectMapper.readValue(response, TodoListResponseDto.class).complete();
            for (Long checklistId : completeList) {
                memberService.completeChecklist(checklistId, memberId);
            }
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
            EmergencyCheckDto result = objectMapper.readValue(response, EmergencyCheckDto.class);
            return result;
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
