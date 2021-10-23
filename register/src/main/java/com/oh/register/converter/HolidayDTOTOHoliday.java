package com.oh.register.converter;

import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.model.entity.Holiday;
import com.oh.register.repository.EmployeeRepository;
import com.oh.register.service.SearchBusinessDay;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HolidayDTOTOHoliday {

    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;
    private final SearchBusinessDay searchBusinessDay;

    @Autowired
    public HolidayDTOTOHoliday(ModelMapper modelMapper, EmployeeRepository employeeRepository, SearchBusinessDay searchBusinessDay) {
        this.modelMapper = modelMapper;
        this.employeeRepository = employeeRepository;
        this.searchBusinessDay = searchBusinessDay;
    }

    public Holiday getHoliday(HolidayDTO holidayDTO) {
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getFinishDate())) {
            throw new RegisterException("The start day must be earlier than the finish day!");
        }
        Holiday holiday;
        Long id = holidayDTO.getEmployeeId();
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty())
            throw new RegisterException("The employee entity does not exist with id: " + id + "!");
        else {
            //searchBusinessDay.searchBusinessDay(holidayDTO);
            Employee employee = employeeOptional.get();
            if (holidayDTO.getStartDate().isBefore(employee.getBeginningOfEmployment())) {
                throw new RegisterException("The beginning of employment must be earlier than the start day!");
            }
            holiday = modelMapper.map(holidayDTO, Holiday.class);
            holiday.getLocalDateStorage().put(holidayDTO.getStartDate(), holidayDTO.getFinishDate());
            holiday.setEmployee(employee);
        }
        return holiday;
    }
}
