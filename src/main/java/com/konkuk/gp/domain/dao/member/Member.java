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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mb_id")
    private Long id;

    @Column(name = "mb_name")
    private String name;

    @OneToMany(mappedBy = "member")
    private List<MemberDisease> diseaseList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<MemberChecklist> checklistList = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PreferredFood> foodList = new ArrayList<>();
}
