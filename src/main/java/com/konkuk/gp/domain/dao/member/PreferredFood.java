package com.konkuk.gp.domain.dao.member;

import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class PreferredFood {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pfood_id")
    private Long id;

    @Column(name = "pfood_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "pfood_member_id")
    private Member member;

    @Builder
    public PreferredFood(String name, Member member) {
        this.name = name;
        this.member = member;
        if (!member.getFoodList().contains(this)) {
            member.getFoodList().add(this);
        }
    }
}
