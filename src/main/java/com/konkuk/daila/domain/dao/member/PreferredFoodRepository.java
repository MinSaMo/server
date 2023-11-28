package com.konkuk.daila.domain.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferredFoodRepository extends JpaRepository<PreferredFood, Long> {
    boolean existsByNameAndMemberId(String name, Long memberId);
}
