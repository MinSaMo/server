package com.konkuk.daila.config;

import com.konkuk.daila.domain.dao.DiseaseRepository;
import com.konkuk.daila.domain.dao.Todolist;
import com.konkuk.daila.domain.dao.TodolistRepository;
import com.konkuk.daila.domain.dao.member.*;
import com.konkuk.daila.global.logger.UserInformationLogProperty;
import com.konkuk.daila.service.MailService;
import com.konkuk.daila.service.TodolistService;
import com.konkuk.daila.service.dialog.DialogService;
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

    private final DialogService dialogManager;
    private final MailService mailService;
    private final UserInformationLogProperty logProperty;
    private final TodolistService todolistService;

    @PostConstruct
    @Transactional
    public void init() {
        Member member = memberRepository.save(Member.builder()
                .name("권순재")
                .build());
        dialogManager.setMemberId(member.getId());
        logProperty.setMemberId(member.getId());
        mailService.setMemberId(member.getId());

        dialogManager.setGptHistory(new ArrayList<>());

        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("매운 음식")
                .build()));
        foodRepository.save(foodRepository.save(PreferredFood.builder()
                .member(member)
                .name("일식")
                .build()));

        Todolist todolist = todolistRepository.save(Todolist.builder()
                .description("일곱 난쟁이팀 응원하기")
                .deadline(LocalDateTime.of(2023, 12, 4, 1, 55))
                .build());
        MemberTodolist saved = memberTodolistRepository.save(MemberTodolist.builder()
                .todolist(todolist)
                .member(member)
                .build());
        todolistService.scheduleTodoList(saved.getTodolist().getId());

//        Disease dis = diseaseRepository.save(Disease.builder()
//                .name("조현정동장애")
//                .build());
//
//        memberDiseaseRepository.save(MemberDisease.builder()
//                .disease(dis)
//                .member(member)
//                .build());

        logProperty.load();
    }

}
