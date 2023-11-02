package com.konkuk.gp.controller.http;

import com.konkuk.gp.config.DatabaseInitConfiguration;
import com.konkuk.gp.controller.http.dto.ClientMessageDto;
import com.konkuk.gp.controller.http.dto.DialogDto;
import com.konkuk.gp.service.dialog.DialogManager;
import com.konkuk.gp.service.SessionRegistry;
import com.konkuk.gp.service.enums.SessionType;
import com.konkuk.gp.controller.http.dto.CaptionDto;
import com.konkuk.gp.global.exception.NotFoundException;
import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final SessionRegistry sessionRegistry;

    private final DialogManager dialogManager;
    private final DatabaseInitConfiguration configuration;

    @GetMapping("/init")
    public void init() {
        configuration.init();
    }
    @PostMapping("/script")
    public String sendMessageToClient(@RequestBody ClientMessageDto dto) {
        WebSocketSession session = sessionRegistry.getSession(SessionType.CLIENT)
                .orElseThrow(() -> NotFoundException.CLIENT_SESSION_NOT_EXIST);
        // 채팅 로그 찍어주기
//        clientSocketHandler.sendAdminMessage(session, Message.of(dto.script(), Message.STATUS_OK));
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
        return "OK";
    }
}
