package com.konkuk.gp.domain.dao.member;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "mb_id")
    private Long id;

    @Column(name = "mb_name")
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<MemberDisease> diseaseList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<MemberTodolist> checklistList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<PreferredFood> foodList = new ArrayList<>();

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
