package com.konkuk.gp.config;

import com.konkuk.gp.domain.dao.Checklist;
import com.konkuk.gp.domain.dao.ChecklistRepository;
import com.konkuk.gp.domain.dao.member.MemberChecklist;
import com.konkuk.gp.domain.dao.member.MemberChecklistRepository;
import com.konkuk.gp.service.dialog.DialogManager;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitConfiguration {
    private final MemberRepository memberRepository;
    private final ChecklistRepository checklistRepository;
    private final MemberChecklistRepository memberChecklistRepository;
    private final DialogManager dialogManager;

    @PostConstruct
    @Transactional
    public void init() {
        memberRepository.deleteAll();
        Member member = memberRepository.save(Member.builder()
                .name("testMember")
                .build());
        dialogManager.setMemberId(member.getId());
        log.info("Member : {}", member.toString());

        Checklist checklist = checklistRepository.save(Checklist.builder()
                .isComplete(false)
                .description("cook")
                .deadline(LocalDateTime.now().plusDays(1))
                .build());
        memberChecklistRepository.save(MemberChecklist.builder()
                .checklist(checklist)
                .member(member)
                .build());
    }

}
