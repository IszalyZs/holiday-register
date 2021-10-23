package com.oh.register.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oh.register.model.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {
    private Long id;

    @OneToOne
    @JsonIgnoreProperties
    private EmployeeDTO employeeDTO;

    @NotNull(message = "The first name field can not be empty!")
    private LocalDate startDate;

    @NotNull(message = "The first name field can not be empty!")
    private LocalDate finishDate;
}
