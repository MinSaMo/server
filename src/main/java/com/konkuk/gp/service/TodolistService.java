package com.konkuk.gp.service;

import com.konkuk.gp.domain.dao.Todolist;
import com.konkuk.gp.domain.dao.TodolistRepository;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberTodolist;
import com.konkuk.gp.domain.dao.member.MemberTodolistRepository;
import com.konkuk.gp.domain.dto.request.TodolistCreateDto;
import com.konkuk.gp.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodolistService {

    private final TodolistRepository checklistRepository;
    private final MemberTodolistRepository memberChecklistRepository;

    @Transactional
    public String completeTodolist(Long checklistId, Member member) {
        MemberTodolist checklist = member.getChecklistList().stream()
                .filter(mc -> mc.getChecklist().getId().equals(checklistId))
                .findFirst()
                .orElseThrow(() -> NotFoundException.TODOLIST_NOT_FOUND);
        checklist.setComplete();
        log.info("Complete Todolist : {}", checklist);
        return checklist.getChecklist().getDescription();
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto) {
        Todolist checklist = toEntity(dto);
        return checklistRepository.save(checklist);
    }

    @Transactional
    public Todolist saveTodolist(TodolistCreateDto dto, Member member) {
        Todolist checklist = this.saveTodolist(dto);
        MemberTodolist memberChecklist = MemberTodolist.builder()
                .checklist(checklist)
                .member(member)
                .build();
        memberChecklistRepository.save(memberChecklist);
        return checklist;
    }

    public Todolist toEntity(TodolistCreateDto dto) {
        return Todolist.builder()
                .deadline(dto.deadline())
                .description(dto.description())
                .build();
    }
}
