package com.konkuk.gp.core.socket.handler;

import com.konkuk.gp.core.gpt.GptService;
import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.message.dto.ai.AiRequestDto;
import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.global.Utils;
import com.konkuk.gp.global.exception.socket.ErrorMessage;
import com.konkuk.gp.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@Component
@Qualifier("ai")
public class AiSocketHandler extends TextMessageHandler {

    private final DialogManager dialogManager;
    private final MemberService memberService;
    @Autowired
    public AiSocketHandler(SessionRegistry registry, GptService chatGptService, DialogManager dialogManager, MemberService memberService) {
        super(registry, SessionType.AI, chatGptService);
        this.dialogManager = dialogManager;
        this.memberService = memberService;
    }

    /***
     * @function
     * 1. 사용자가 todolist에서 한 행위가 있는가 체크
     * 2. 사용자가 하고 있는 행위가 조언이 필요한 행위인가 체크
     *  -> 2번이면 대화 넘기면 되고
     * 3. 과연 그러면 daily를 지원할건가
     *  3-1. 서있거나, 앉아있거나, 누워있거나
     *      -> 룰 기반으로 특정 행동 인지 시 확인
     *  3-2. 간섭정도를 정할 수 있는가
     * 4. emergency
     *  -> 넘어졌다, 쓰러졌다라는 캡션이 나오면 클라이언트에게 위급상황 확인요청 전송
     *  -> 이것도 룰 기반의 특정 행동양식 인지
     *  -> 클라이언트가 알아서 하다가, 나한테 위급상황 매뉴얼 수행 요청
     */

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) {

        Long memberId = dialogManager.getMemberId();
        Message<AiRequestDto> message;
        try {
            message = Utils.getObject(textMessage.getPayload(), AiRequestDto.class);
        } catch (Exception e) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        if (!message.getSender().equals(Message.SENDER_AI)) {
            sendError(session, ErrorMessage.INVALID_MESSAGE_FORMAT);
            return;
        }

        AiRequestDto data = message.getData();
        log.info("[AI] Received caption : " + data.getCaption());

        /**
         * @function
         * @ 2023.09.09
         * 현재 todolist 체크만 하고 종료
         * - 대화 시작 여부도 체크
         * - 응급 상황 체크
         */

        List<MemberChecklist> todolist = memberService.getTodolist(memberId);
        chatGptService.checkCompletedTodolist(data.getCaption(), memberId);

        Message<String> response = Message.of("OK");
        sendMessage(session, response);
    }
}
