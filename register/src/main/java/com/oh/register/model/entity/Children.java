package com.oh.register.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity(name = "Children")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Children {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The first name field can not be empty!")
    private String firstName;

    @NotBlank(message = "The last name field can not be empty!")
    private String lastName;

    @NotNull(message = "The birthday field can not be empty!")
    private LocalDate birthDay;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Employee employee;


}
