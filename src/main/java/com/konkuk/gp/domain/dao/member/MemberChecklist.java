package com.konkuk.gp.domain.dao.member;

import com.konkuk.gp.domain.dao.Checklist;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class MemberChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mc_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mc_member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "mc_checklist_id")
    private Checklist checklist;

    @Builder
    public MemberChecklist(Member member, Checklist checklist) {
        this.member = member;
        this.checklist = checklist;

        if (!member.getDiseaseList().contains(this)) {
            member.getChecklistList().add(this);
        }
    }
}
