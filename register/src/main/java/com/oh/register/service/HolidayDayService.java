package com.oh.register.service;

import com.oh.register.converter.HolidayDayDTOToHolidayDay;
import com.oh.register.converter.HolidayDayToHolidayDTODay;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDayDTO;
import com.oh.register.model.entity.HolidayDay;
import com.oh.register.repository.HolidayDayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HolidayDayService {
    private final HolidayDayRepository holidayDayRepository;
    private final HolidayDayDTOToHolidayDay holidayDayDTOToHolidayDay;
    private final HolidayDayToHolidayDTODay holidayDayToHolidayDTODay;

    @Autowired
    public HolidayDayService(HolidayDayRepository holidayDayRepository, HolidayDayDTOToHolidayDay holidayDayDTOToHolidayDay, HolidayDayToHolidayDTODay holidayDayToHolidayDTODay) {
        this.holidayDayRepository = holidayDayRepository;
        this.holidayDayDTOToHolidayDay = holidayDayDTOToHolidayDay;
        this.holidayDayToHolidayDTODay = holidayDayToHolidayDTODay;
    }

    public List<HolidayDayDTO> findAll() {
        List<HolidayDay> holidayDays = holidayDayRepository.findAll();
        if (holidayDays.size() == 0) {
            throw new RegisterException("The holiday entities don't exist!");
        }
        return holidayDays.stream().map(holidayDayToHolidayDTODay::getHolidayDTO).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        try {
            holidayDayRepository.deleteById(id);
        } catch (Exception exception) {
            throw new RegisterException("No holiday entity with id: " + id + "!");
        }
    }


    public HolidayDayDTO findById(Long id) {
        Optional<HolidayDay> optionalHoliday = holidayDayRepository.findById(id);
        if (optionalHoliday.isPresent()) {
            HolidayDay holidayDay = optionalHoliday.get();
            return holidayDayToHolidayDTODay.getHolidayDTO(holidayDay);
        }
        throw new RegisterException("The holiday entity doesn't exist with id: " + id + "!");
    }

    public HolidayDayDTO save(HolidayDayDTO holidayDayDTO) {
        HolidayDay holidayDay = holidayDayRepository.save(holidayDayDTOToHolidayDay.getHoliday(holidayDayDTO));
        return holidayDayToHolidayDTODay.getHolidayDTO(holidayDay);
    }

    public HolidayDayDTO update(HolidayDayDTO holidayDayDTO) {
        HolidayDay holidayDay = holidayDayRepository.save(holidayDayDTOToHolidayDay.getHoliday(holidayDayDTO));
        return holidayDayToHolidayDTODay.getHolidayDTO(holidayDay);
    }
}
