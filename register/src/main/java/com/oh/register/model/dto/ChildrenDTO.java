package com.oh.register.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oh.register.model.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildrenDTO {
    private Long id;

    @NotBlank(message = "The first name field can not be empty!")
    private String firstName;

    @NotBlank(message = "The last name field can not be empty!")
    private String lastName;

    @Past(message = "Date of birth must be less than today!")
    @NotNull(message = "The birthday field can not be empty!")
    private LocalDate birthDay;

    @ManyToOne
    @JsonIgnoreProperties({"firstName","lastName","identityNumber","workplace","position","timeOfEntry","beginningOfEmployment","childrenList"})
    private Employee employee;
}
