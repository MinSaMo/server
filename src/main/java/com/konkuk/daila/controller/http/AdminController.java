package com.konkuk.daila.controller.http;

import com.konkuk.daila.config.DatabaseInitConfiguration;
import com.konkuk.daila.controller.http.dto.CaptionDto;
import com.konkuk.daila.controller.http.dto.ClientMessageDto;
import com.konkuk.daila.controller.http.dto.DialogDto;
import com.konkuk.daila.global.exception.NotFoundException;
import com.konkuk.daila.service.SessionRegistry;
import com.konkuk.daila.service.dialog.DialogService;
import com.konkuk.daila.service.dialog.Message;
import com.konkuk.daila.service.enums.SessionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final SessionRegistry sessionRegistry;

    private final DialogService dialogService;
    private final DatabaseInitConfiguration configuration;

    @GetMapping("/init")
    public void init() {
        configuration.init();
    }
    @PostMapping("/script")
    public String sendMessageToClient(@RequestBody ClientMessageDto dto) {
        WebSocketSession session = sessionRegistry.getSession(SessionType.CLIENT)
                .orElseThrow(() -> NotFoundException.CLIENT_SESSION_NOT_EXIST);
        dialogService.addMessage(Message.ofAssistant(dto.script()));
        dialogService.saveDialogHistory();
        return "OK";
    }

    @PostMapping("/dialog")
    public String sendDialog(@RequestBody DialogDto dto) {
        if (!dialogService.hasDialog()) {
            dialogService.startDialog();
        }
        dialogService.addMessage(Message.ofUser(dto.script()));
        return "OK";
    }

    @GetMapping("/dialog_end")
    public ResponseEntity endDialog() {
        dialogService.endDialog();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/caption")
    public String sendCaption(@RequestBody CaptionDto dto) {
        return "OK";
    }
}
