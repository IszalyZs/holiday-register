package com.oh.register.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayDTO {
    private Long id;

    @NotNull(message = "The year field can not be empty!")
    @Column(unique = true)
    private int year;

    private List<LocalDate> localDate = new ArrayList<>();
}
