package com.konkuk.daila.domain.dao.member;

import com.konkuk.daila.domain.dao.Todolist;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class MemberTodolist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mc_id")
    private Long id;

    @Setter(AccessLevel.PROTECTED)
    @Column(name = "mc_isCompleted")
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "mc_member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "mc_checklist_id")
    private Todolist todolist;

    @Builder
    public MemberTodolist(Member member, Todolist todolist) {
        this.member = member;
        this.todolist = todolist;
        isCompleted = false;

        if (!member.getDiseaseList().contains(this)) {
            member.getChecklistList().add(this);
        }
    }

    public void setComplete() {
        this.isCompleted = true;
    }

    @Override
    public String toString() {
        return "MemberChecklist{" +
                "id:" + id +
                ", isCompleted:" + isCompleted +
                ", checklist=" + todolist +
                '}';
    }
}

