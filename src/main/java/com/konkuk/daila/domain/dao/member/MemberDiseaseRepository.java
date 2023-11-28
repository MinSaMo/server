package com.konkuk.daila.domain.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDiseaseRepository extends JpaRepository<MemberDisease, Long> {
    boolean existsByDiseaseNameAndMemberId(String diseaseName, Long memberId);
}