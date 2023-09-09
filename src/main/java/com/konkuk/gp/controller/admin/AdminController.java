package com.konkuk.gp.controller.admin;

import com.konkuk.gp.core.message.Message;
import com.konkuk.gp.core.socket.SessionRegistry;
import com.konkuk.gp.core.socket.SessionType;
import com.konkuk.gp.core.socket.handler.ClientSocketHandler;
import com.konkuk.gp.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ClientSocketHandler clientSocketHandler;
    private final SessionRegistry sessionRegistry;

    @PostMapping("/send")
    public String sendMessageToClient(@RequestBody ClientMessageDto dto) {
        WebSocketSession session = sessionRegistry.getSession(SessionType.CLIENT)
                .orElseThrow(() -> NotFoundException.CLIENT_SESSION_NOT_EXIST);
        clientSocketHandler.sendAdminMessage(session, Message.of(dto.script()));
        return "OK";
    }
}
