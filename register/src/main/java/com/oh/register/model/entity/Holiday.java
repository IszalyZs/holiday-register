package com.oh.register.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "Holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_id")
    private Long id;

    @ManyToOne
    private Employee employee;

    private LocalDate startDate;

    private LocalDate finishDate;
}
