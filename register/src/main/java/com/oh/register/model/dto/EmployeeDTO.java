package com.oh.register.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Long id;

    @NotBlank(message = "The first name field can not be empty!")
    private String firstName;

    @NotBlank(message = "The last name field can not be empty!")
    private String lastName;

    @Pattern(regexp = "[0-9]{9}", message = "The identity number length should be exactly 9 numbers!")
    @Column(unique = true)
    private String identityNumber;

    @NotNull(message = "The birth date field can not be empty!")
    private LocalDate birthDate;

    @NotBlank(message = "The workplace field can not be empty!")
    private String workplace;

    @NotBlank(message = "The position field can not be empty!")
    private String position;

    @NotNull(message = "The time of entry field can not be empty!")
    private LocalDate dateOfEntry;

    private LocalDate beginningOfEmployment;

    @OneToMany(mappedBy = "employeeDTO", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ChildrenDTO> childrenList = new ArrayList<>();

    @OneToOne
    @JsonIgnore
    private HolidayDTO holidayDTO;

}
