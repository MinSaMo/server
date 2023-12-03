package com.konkuk.daila.domain.dao;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
public class Todolist {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "td_id")
    private Long id;

    @Column(name = "td_descrption")
    private String description;

    @Column(name = "td_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime deadline;

    @Override
    public String toString() {
        return "{" +
                "description:'" + description + '\'' +
                ", deadline:" + deadline +
                '}';
    }
}
