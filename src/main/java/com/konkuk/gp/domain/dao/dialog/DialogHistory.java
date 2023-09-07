package com.konkuk.gp.domain.dao.dialog;

import io.github.flashvayne.chatgpt.dto.chat.MultiChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Builder
@AllArgsConstructor
public class DialogHistory {
    private Long memberId;
    private List<MultiChatMessage> chats;
}
