package com.oh.register.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The first name field can not be empty!")
    private String firstName;

    @NotBlank(message = "The last name field can not be empty!")
    private String lastName;

    @Pattern(regexp = "[0-9]{9}", message = "The identity number length should be exactly 9 numbers!")
    @Column(unique = true)
    private String identityNumber;

    @NotBlank(message = "The workplace field can not be empty!")
    private String workplace;

    @NotBlank(message = "The position field can not be empty!")
    private String position;

    @NotNull(message = "The time of entry field can not be empty!")
    private LocalDate timeOfEntry;

    private LocalDate beginningOfEmployment;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Children> childrenList = new ArrayList<>();


}
