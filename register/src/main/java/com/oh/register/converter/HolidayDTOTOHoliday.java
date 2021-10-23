package com.oh.register.converter;

import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Holiday;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HolidayDTOTOHoliday {

    private final ModelMapper modelMapper;

    @Autowired
    public HolidayDTOTOHoliday(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Holiday getHoliday(HolidayDTO holidayDTO){
        Holiday holiday = modelMapper.map(holidayDTO, Holiday.class);
        holiday.getLocalDateStorage().put(holiday.getStartDate(),holiday.getFinishDate());
        return holiday;
    }
}
