package com.konkuk.gp.domain.dao.member;

import com.konkuk.gp.domain.dao.Disease;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class MemberDisease {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "md_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "md_member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "md_disease_id")
    private Disease disease;

    @Builder
    public MemberDisease(Member member, Disease disease) {
        this.member = member;
        this.disease = disease;

        if (!member.getDiseaseList().contains(this)) {
            member.getDiseaseList().add(this);
        }
    }
}
