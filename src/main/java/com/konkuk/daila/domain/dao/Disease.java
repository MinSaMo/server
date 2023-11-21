package com.konkuk.daila.domain.dao;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ds_id")
    private Long id;

    @Column(name = "ds_name")
    private String name;
}
