package com.oh.register.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Employee")
@Getter
@Setter
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
    private LocalDate dateOfEntry;

    private LocalDate beginningOfEmployment;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"employee"})
    private List<Children> childrenList = new ArrayList<>();

    @OneToOne
    @JsonIgnore
    private Holiday holiday;

    @NotNull(message = "The maxHolidayOfYear of entry field can not be empty!")
    @Min(value = 20, message = "The value cannot be less than 20!")
    private Integer basicLeave;

    private Long extraLeave=0L;

    private Long sumHoliday=0L;

}
