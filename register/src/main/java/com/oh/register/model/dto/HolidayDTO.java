package com.oh.register.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {

    private Long id;

    @JsonIgnore
    private Long employeeId;

    @NotNull(message = "The first name field can't be empty!")
    private LocalDate startDate;

    @NotNull(message = "The first name field can't be empty!")
    private LocalDate finishDate;
}
