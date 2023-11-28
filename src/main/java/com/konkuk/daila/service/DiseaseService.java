package com.konkuk.daila.service;

import com.konkuk.daila.domain.dao.Disease;
import com.konkuk.daila.domain.dao.DiseaseRepository;
import com.konkuk.daila.domain.dao.member.Member;
import com.konkuk.daila.domain.dao.member.MemberDisease;
import com.konkuk.daila.domain.dao.member.MemberDiseaseRepository;
import com.konkuk.daila.domain.dto.request.DiseaseCreateDto;
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

        if (memberDiseaseRepository.existsByDiseaseNameAndMemberId(dto.name(), member.getId())) {
            return null;
        }
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
