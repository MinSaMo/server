package com.konkuk.daila.service;

import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dao.TodolistRepository;
import com.konkuk.daila.domain.dao.member.Member;
import com.konkuk.daila.domain.dao.member.MemberTodolist;
import com.konkuk.daila.domain.dao.member.MemberTodolistRepository;
import com.konkuk.daila.domain.dto.request.TodolistCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodolistService {

    private final TodolistRepository checklistRepository;
    private final MemberTodolistRepository memberChecklistRepository;

    @Transactional
    public String completeTodolist(Long todoListId) {
        MemberTodolist checklist = memberChecklistRepository.findByTodolistId(todoListId)
                .orElseThrow(() -> new RuntimeException("Not found todolist"));
        checklist.setComplete();
        log.info("Complete Todolist : {}", checklist);
        return checklist.getTodolist().getDescription();
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto) {
        Todolist checklist = toEntity(dto);
        return checklistRepository.save(checklist);
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto, Member member) {
        if (memberChecklistRepository.existsByTodolistDescriptionAndMemberId(dto.description(), member.getId())) {
            return null;
        }
        Todolist todolist = this.saveTodolist(dto);
        MemberTodolist memberChecklist = MemberTodolist.builder()
                .todolist(todolist)
                .member(member)
                .build();
        memberChecklistRepository.save(memberChecklist);
        return todolist;
    }

    @Transactional
    public List<Todolist> findAllByMemberId(Long memberId) {
        return memberChecklistRepository.findByMemberId(memberId).stream()
                .map(MemberTodolist::getTodolist)
                .toList();
    }

    public Todolist toEntity(TodolistCreateDto dto) {
        return Todolist.builder()
                .deadline(dto.deadline())
                .description(dto.description())
                .build();
    }
}
