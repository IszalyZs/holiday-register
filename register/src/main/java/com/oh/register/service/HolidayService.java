package com.oh.register.service;

import com.oh.register.converter.HolidayDTOTOHoliday;
import com.oh.register.converter.HolidayToHolidayDTO;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Holiday;
import com.oh.register.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayDTOTOHoliday holidayDTOTOHoliday;
    private final HolidayToHolidayDTO holidayToHolidayDTO;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, HolidayDTOTOHoliday holidayDTOTOHoliday, HolidayToHolidayDTO holidayToHolidayDTO) {
        this.holidayRepository = holidayRepository;
        this.holidayDTOTOHoliday = holidayDTOTOHoliday;
        this.holidayToHolidayDTO = holidayToHolidayDTO;
    }

    public HolidayDTO save(HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.save(holidayDTOTOHoliday.getHoliday(holidayDTO));
        return holidayToHolidayDTO.getHolidayDTO(holiday);
    }
}
