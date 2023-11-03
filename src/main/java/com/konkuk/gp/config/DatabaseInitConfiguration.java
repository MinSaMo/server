package com.konkuk.gp.config;

import com.konkuk.gp.domain.dao.Disease;
import com.konkuk.gp.domain.dao.DiseaseRepository;
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
import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitConfiguration {
    private final MemberRepository memberRepository;

    private final TodolistRepository todolistRepository;
    private final MemberTodolistRepository memberTodolistRepository;

    private final PreferredFoodRepository foodRepository;
    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;

    private final DialogManager dialogManager;
    private final UserInformationLogProperty logProperty;

    @PostConstruct
    @Transactional
    public void init() {
        Member member = memberRepository.save(Member.builder()
                .name("권순재")
                .build());
        dialogManager.setMemberId(member.getId());
        logProperty.setMemberId(member.getId());
        /*
        reset ahnLLM history
         */
        dialogManager.setGptHistory(new ArrayList<>());

        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("매운 음식")
                .build()));
        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("일식")
                .build()));

        Todolist checklist = todolistRepository.save(Todolist.builder()
                .description("일곱 난쟁이팀 응원하기")
                .deadline(LocalDateTime.of(2023, 11, 3, 17, 0))
                .build());
        memberTodolistRepository.save(MemberTodolist.builder()
                .checklist(checklist)
                .member(member)
                .build());

        Disease dis = diseaseRepository.save(Disease.builder()
                .name("조현정동장애")
                .build());

        memberDiseaseRepository.save(MemberDisease.builder()
                .disease(dis)
                .member(member)
                .build());

        logProperty.load();
    }

}
