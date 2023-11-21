package com.konkuk.daila.global.logger;

import com.konkuk.daila.global.logger.message.ai.AiLog;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Scope("prototype")
@Setter
public class AiLogProperty implements AiLog {
    private UUID uuid;
    private Long memberId;
    private String caption;
    private LocalDateTime emergencyCheckTimestamp;
    private LocalDateTime emergencyOccurTimestamp;
    @Setter(AccessLevel.PROTECTED)
    private List<String> completedTodolist;
    private String reply;

    public void setCompleteTodolist(String todolist) {
        completedTodolist.add(todolist);
    }

    public void setCompleteTodolist(List<String> todolist) {
        completedTodolist.addAll(todolist);
    }

    @Override
    public Map<String, Object> getCaptionLogMessage() {
        Map<String, Object> res = base();
        res.put("caption", caption);
        return res;
    }

    @Override
    public Map<String, Object> getEmergencyCheckLogMessage() {
        Map<String, Object> res = getCaptionLogMessage();
        res.put("emergencyCheckTimestamp", emergencyCheckTimestamp.toString());
        return res;
    }

    @Override
    public Map<String, Object> getEmergencyOccurLogMessage() {
        Map<String, Object> res = getCaptionLogMessage();
        res.put("emergencyOccurTimestamp", emergencyOccurTimestamp.toString());
        return res;
    }

    @Override
    public Map<String, Object> getTodoCompleteLogMessage() {
        Map<String, Object> res = getCaptionLogMessage();
        res.put("completeTodo", completedTodolist);
        return res;
    }

    @Override
    public Map<String, Object> getReplyLogMessage() {
        Map<String, Object> res = getCaptionLogMessage();
        res.put("reply", reply);
        return res;
    }

    private Map<String, Object> base() {
        Map<String, Object> res = new HashMap<>();
        res.put("timestamp", LocalDateTime.now().toString());
        res.put("uuid", uuid.toString());
        res.put("memberId", String.valueOf(memberId));
        return res;
    }
}
