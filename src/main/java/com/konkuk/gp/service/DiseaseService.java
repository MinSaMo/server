package com.konkuk.gp.service;

import com.konkuk.gp.domain.dao.Disease;
import com.konkuk.gp.domain.dao.DiseaseRepository;
import com.konkuk.gp.domain.dao.member.Member;
import com.konkuk.gp.domain.dao.member.MemberDisease;
import com.konkuk.gp.domain.dao.member.MemberDiseaseRepository;
import com.konkuk.gp.domain.dto.request.DiseaseCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final MemberDiseaseRepository memberDiseaseRepository;

    @Transactional
    public Disease saveDisease(DiseaseCreateDto dto) {
        Disease disease = toEntity(dto);
        return diseaseRepository.save(disease);
    }

    @Transactional
    public Disease saveDisease(DiseaseCreateDto dto, Member member) {
        Disease disease = saveDisease(dto);

        MemberDisease memberDisease = MemberDisease.builder()
                .member(member)
                .disease(disease)
                .build();

        memberDiseaseRepository.save(memberDisease);
        return disease;
    }

    public Disease toEntity(DiseaseCreateDto dto) {
        return Disease.builder()
                .name(dto.name())
                .build();
    }

}
