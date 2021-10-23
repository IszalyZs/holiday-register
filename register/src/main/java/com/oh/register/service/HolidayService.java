package com.oh.register.service;

import com.oh.register.converter.HolidayDTOTOHoliday;
import com.oh.register.converter.HolidayToHolidayDTO;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.model.entity.Holiday;
import com.oh.register.repository.EmployeeRepository;
import com.oh.register.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayDTOTOHoliday holidayDTOTOHoliday;
    private final HolidayToHolidayDTO holidayToHolidayDTO;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, HolidayDTOTOHoliday holidayDTOTOHoliday, HolidayToHolidayDTO holidayToHolidayDTO, EmployeeRepository employeeRepository) {
        this.holidayRepository = holidayRepository;
        this.holidayDTOTOHoliday = holidayDTOTOHoliday;
        this.holidayToHolidayDTO = holidayToHolidayDTO;
        this.employeeRepository = employeeRepository;
    }

    public HolidayDTO save(HolidayDTO holidayDTO) {
        Holiday holiday = holidayRepository.save(holidayDTOTOHoliday.getHoliday(holidayDTO));
        Employee employee = holiday.getEmployee();
        employee.setHoliday(holiday);
        employeeRepository.save(employee);
        return holidayToHolidayDTO.getHolidayDTO(holiday);
    }

    public void delete(HolidayDTO holidayDTO) {
        Holiday holiday = holidayDTOTOHoliday.getHoliday(holidayDTO);
        Holiday holidayByStartDate = holidayRepository.findByStartDate(holiday.getStartDate());
        Holiday holidayByFinishDate = holidayRepository.findByFinishDate(holiday.getFinishDate());
        if (holidayByStartDate.equals(holidayByFinishDate)) {
            holidayRepository.deleteById(holidayByStartDate.getId());
        } else throw new RegisterException("The specified interval does not exist!");
    }
}
