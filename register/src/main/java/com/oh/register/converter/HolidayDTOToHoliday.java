package com.oh.register.converter;

import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Holiday;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HolidayDTOToHoliday {
    private ModelMapper modelMapper;

    @Autowired
    public HolidayDTOToHoliday(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Holiday getHoliday(HolidayDTO holidayDTO) {
        return modelMapper.map(holidayDTO, Holiday.class);
    }
}
