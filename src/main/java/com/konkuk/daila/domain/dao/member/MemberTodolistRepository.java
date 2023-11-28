package com.konkuk.daila.domain.dao.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberTodolistRepository extends JpaRepository<MemberTodolist, Long> {
    List<MemberTodolist> findByMemberId(Long memberId);

    Optional<MemberTodolist> findByTodolistId(Long todolistId);

    boolean existsByTodolistDescriptionAndMemberId(String todolistDescription, Long memberId);
}
