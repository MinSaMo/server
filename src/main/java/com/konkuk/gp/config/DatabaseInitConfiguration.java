package com.konkuk.gp.config;

import com.konkuk.gp.domain.dao.Todolist;
import com.konkuk.gp.domain.dao.TodolistRepository;
import com.konkuk.gp.domain.dao.member.*;
import com.konkuk.gp.global.logger.UserInformationLogProperty;
import com.konkuk.gp.service.dialog.DialogManager;
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

    private final TodolistRepository todolistRepository;
    private final MemberTodolistRepository memberTodolistRepository;

    private final PreferredFoodRepository foodRepository;

    private final DialogManager dialogManager;
    private final UserInformationLogProperty logProperty;

    @PostConstruct
    @Transactional
    public void init() {
        memberRepository.deleteAll();
        Member member = memberRepository.save(Member.builder()
                .name("testMember")
                .build());
        dialogManager.setMemberId(member.getId());
        logProperty.setMemberId(member.getId());
        log.info("[Service Mock Init] Member : {}", member.toString());

        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("매운 음식 선호")
                .build()));

        log.info("[Service Mock Init] food : {}", "매운 음식 선호");

        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("버섯이 들어간 음식 비선호")
                .build()));
        log.info("[Service Mock Init] food : {}", "버섯이 들어간 음식 비선호");

        Todolist checklist = todolistRepository.save(Todolist.builder()
                .description("cook")
                .deadline(LocalDateTime.now().plusDays(1))
                .build());
        memberTodolistRepository.save(MemberTodolist.builder()
                .checklist(checklist)
                .member(member)
                .build());
        log.info("[Service Mock Init] todo : {}", "cook");

        logProperty.load();
    }

}
