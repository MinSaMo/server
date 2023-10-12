package com.konkuk.gp.controller.admin;

import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.handler.ClientSocketHandler;
import com.konkuk.gp.core.socket.handler.dashboard.CaptionDto;
import com.konkuk.gp.core.socket.handler.dashboard.CaptionLoggerHandler;
import com.konkuk.gp.global.exception.NotFoundException;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

@Tag(name = "Admin API", description = "데모용 admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ClientSocketHandler clientSocketHandler;
    private final SessionRegistry sessionRegistry;

    private final DialogManager dialogManager;
    private final CaptionLoggerHandler captionLoggerHandler;

    @PostMapping("/script")
    public String sendMessageToClient(@RequestBody ClientMessageDto dto) {
        WebSocketSession session = sessionRegistry.getSession(SessionType.CLIENT)
                .orElseThrow(() -> NotFoundException.CLIENT_SESSION_NOT_EXIST);
        // 채팅 로그 찍어주기
        clientSocketHandler.sendAdminMessage(session, Message.of(dto.script(), Message.STATUS_OK));
        dialogManager.addMessage(new MultiChatMessage("assistant", dto.script()));
        dialogManager.saveDialogHistory();
        return "OK";
    }

    @PostMapping("/dialog")
    public String sendDialog(@RequestBody DialogDto dto) {
        if (!dialogManager.hasDialog()) {
            dialogManager.startDialog();
        }
        dialogManager.addMessage(new MultiChatMessage("user", dto.script()));
        return "OK";
    }

    @PostMapping("/caption")
    public String sendCaption(@RequestBody CaptionDto dto) {
        captionLoggerHandler.addCaption(dto.caption());
        return "OK";
    }
}
