package com.konkuk.daila.service;

import com.konkuk.daila.controller.stomp.dto.client.ClientResponseDto;
import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dao.TodolistRepository;
import com.konkuk.daila.domain.dao.member.Member;
import com.konkuk.daila.domain.dao.member.MemberTodolist;
import com.konkuk.daila.domain.dao.member.MemberTodolistRepository;
import com.konkuk.daila.domain.dto.request.TodolistCreateDto;
import com.konkuk.daila.domain.dto.response.OneStringResponseDto;
import com.konkuk.daila.global.logger.TopicType;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodolistService {

    private final TodolistRepository todolistRepository;
    private final MemberTodolistRepository memberTodolistRepository;
    private final GptService gptService;
    private final PromptManager promptManager;
    private final TodoScheduler scheduler;
    private final SimpMessagingTemplate template;

    @Transactional
    public String completeTodolist(Long todoListId) {
        MemberTodolist checklist = memberTodolistRepository.findByTodolistId(todoListId)
                .orElseThrow(() -> new RuntimeException("Not found todolist"));
        checklist.setComplete();
        log.info("Complete Todolist : {}", checklist);
        return checklist.getTodolist().getDescription();
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto) {
        Todolist checklist = toEntity(dto);
        return todolistRepository.save(checklist);
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto, Member member) {
        if (memberTodolistRepository.existsByTodolistDescriptionAndMemberId(dto.description(), member.getId())) {
            return null;
        }
        Todolist todolist = this.saveTodolist(dto);
        MemberTodolist memberChecklist = MemberTodolist.builder()
                .todolist(todolist)
                .member(member)
                .build();
        MemberTodolist saved = memberTodolistRepository.save(memberChecklist);
        scheduleTodoList(saved.getTodolist().getId());
        return todolist;
    }

    @Transactional
    public List<Todolist> findAllByMemberId(Long memberId) {
        return memberTodolistRepository.findByMemberId(memberId).stream()
                .map(MemberTodolist::getTodolist)
                .toList();
    }

    @Transactional
    public void scheduleTodoList(Long todoId) {
        Todolist todolist = todolistRepository.findById(todoId)
                .orElseThrow(IllegalAccessError::new);
        LocalDateTime time = todolist.getDeadline();
        if (!time.toLocalDate().equals(LocalDate.now())) return;
        log.info("scheduled todolist : {}", todolist);
        scheduler.schedule(() -> {
            Prompt prompt = promptManager.getTodoAlarmPrompt();
            ChatCompletionRequest request = gptService.request()
                    .addSystemMessage(prompt.getScript())
                    .addUserMessage(todolist.toString())
                    .topP(prompt.getTopP())
                    .temperature(prompt.getTemperature())
                    .build();
            OneStringResponseDto oneStringResponseDto = gptService.askToSub(request, OneStringResponseDto.class);
            log.info("generated todolist alarm script : {}", oneStringResponseDto.response());
            template.convertAndSend(TopicType.SERVICE_REPLY.getPath(), ClientResponseDto.builder()
                    .script(oneStringResponseDto.response())
                    .time(0L)
                    .build());
        }, time);
    }

    public Todolist toEntity(TodolistCreateDto dto) {
        return Todolist.builder()
                .deadline(dto.deadline())
                .description(dto.description())
                .build();
    }
}
