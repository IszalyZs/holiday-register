package com.oh.register.service;

import com.oh.register.converter.HolidayDTOToHoliday;
import com.oh.register.converter.HolidayToHolidayDTO;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Holiday;
import com.oh.register.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayDTOToHoliday holidayDTOToHoliday;
    private final HolidayToHolidayDTO holidayToHolidayDTO;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, HolidayDTOToHoliday holidayDTOToHoliday, HolidayToHolidayDTO holidayToHolidayDTO) {
        this.holidayRepository = holidayRepository;
        this.holidayDTOToHoliday = holidayDTOToHoliday;
        this.holidayToHolidayDTO = holidayToHolidayDTO;
    }

    public List<HolidayDTO> findAll() {
        List<Holiday> holidays = holidayRepository.findAll();
        if (holidays.size() == 0) {
            throw new RegisterException("The holiday entities do not exist!");
        }
        return holidays.stream().map(holidayToHolidayDTO::getHolidayDTO).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        try {
            holidayRepository.deleteById(id);
        } catch (Exception exception) {
            throw new RegisterException("No holiday entity with id: " + id + "!");
        }
    }


    public HolidayDTO findById(Long id) {
        Optional<Holiday> optionalHoliday = holidayRepository.findById(id);
        if (optionalHoliday.isPresent()) {
            Holiday holiday = optionalHoliday.get();
            return holidayToHolidayDTO.getHolidayDTO(holiday);
        }
        throw new RegisterException("The holiday entity does not exist with id: " + id + "!");
    }

    public HolidayDTO save(HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.save(holidayDTOToHoliday.getHoliday(holidayDTO));
        return holidayToHolidayDTO.getHolidayDTO(holiday);
    }

    public HolidayDTO update(HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.save(holidayDTOToHoliday.getHoliday(holidayDTO));
        return holidayToHolidayDTO.getHolidayDTO(holiday);
    }
}
