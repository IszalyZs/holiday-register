package com.oh.register.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity(name = "Children")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "children")
public class Children {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The first name field can not be empty!")
    private String firstName;

    @NotBlank(message = "The last name field can not be empty!")
    private String lastName;

    @Temporal(value=TemporalType.DATE)
    @Column(nullable = false)
    private Date birthDay;

    @ManyToOne
    @JoinColumn(name="employee_id",nullable = false)
    private Employee employee;


}
