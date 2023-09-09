package com.konkuk.gp.service;

import com.konkuk.gp.domain.dao.Checklist;
import com.konkuk.gp.domain.dao.ChecklistRepository;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.domain.dao.member.MemberChecklistRepository;
import com.konkuk.gp.domain.dto.request.ChecklistCreateDto;
import com.konkuk.gp.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final MemberChecklistRepository memberChecklistRepository;

    @Transactional
    public void completeChecklist(Long checklistId, Member member) {
        MemberChecklist checklist = member.getChecklistList().stream()
                .filter(mc -> mc.getChecklist().getId().equals(checklistId))
                .findFirst()
                .orElseThrow(() -> NotFoundException.TODOLIST_NOT_FOUND);
        checklist.setComplete();
        log.info("Complete Todolist : {}", checklist);
    }

    @Transactional
    public Checklist saveChecklist(ChecklistCreateDto dto) {
        Checklist checklist = toEntity(dto);
        return checklistRepository.save(checklist);
    }

    @Transactional
    public Checklist saveChecklist(ChecklistCreateDto dto, Member member) {
        Checklist checklist = this.saveChecklist(dto);
        MemberChecklist memberChecklist = MemberChecklist.builder()
                .checklist(checklist)
                .member(member)
                .build();
        memberChecklistRepository.save(memberChecklist);
        return checklist;
    }

    public Checklist toEntity(ChecklistCreateDto dto) {
        return Checklist.builder()
                .deadline(dto.deadline())
                .description(dto.description())
                .build();
    }
}
