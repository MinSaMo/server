package com.konkuk.daila.domain.dao.dialog;

import com.konkuk.daila.service.dialog.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Builder
@AllArgsConstructor
public class DialogHistory {
    private Long memberId;
    private List<Message> chats;
}
