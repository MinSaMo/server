package com.konkuk.gp.service;

import com.konkuk.gp.domain.dao.member.MemberChecklistRepository;
import com.konkuk.gp.domain.dao.Checklist;
import com.konkuk.gp.domain.dao.ChecklistRepository;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.domain.dto.request.ChecklistCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository checklistRepository;
    private final MemberChecklistRepository memberChecklistRepository;

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
