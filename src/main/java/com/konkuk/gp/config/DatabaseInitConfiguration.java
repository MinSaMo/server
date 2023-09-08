package com.konkuk.gp.config;

import com.konkuk.gp.core.socket.DialogManager;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitConfiguration {
    private final MemberRepository memberRepository;
    private final DialogManager dialogManager;

    @PostConstruct
    protected void init() {
        memberRepository.deleteAll();
        Member member = memberRepository.save(Member.builder()
                .name("testMember")
                .build());
        dialogManager.setMemberId(member.getId());
        log.info("Member : {}", member.toString());
    }
}
