package com.oh.register.service;

import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.dto.HolidayDayDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.model.entity.Holiday;
import com.oh.register.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class SearchBusinessDay {
    private final HolidayDayService holidayDayService;
    private final HolidayRepository holidayRepository;
    private final EmployeeService employeeService;

    @Autowired
    public SearchBusinessDay(HolidayDayService holidayDayService, HolidayRepository holidayRepository, EmployeeService employeeService) {
        this.holidayDayService = holidayDayService;
        this.holidayRepository = holidayRepository;
        this.employeeService = employeeService;
    }

    public void searchBusinessDay(HolidayDTO holidayDTO) {
        LocalDate startDate = holidayDTO.getStartDate();
        LocalDate finishDate = holidayDTO.getFinishDate();
        Employee employee = employeeService.findById(holidayDTO.getId());
        Integer maxHolidayOfYear = employee.getMaxHolidayOfYear();
        Integer sumHoliday = employee.getSumHoliday();
        List<Holiday> holidayList = holidayRepository.findAll();
        Holiday holiday = holidayList.get(holidayList.size() - 1);
        Map<LocalDate, LocalDate> localDateStorage = holiday.getLocalDateStorage();
        //startDate.getYear();
    }
}
