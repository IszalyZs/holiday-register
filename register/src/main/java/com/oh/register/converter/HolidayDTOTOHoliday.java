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
        checkToStartDate(holidayDTO);
        Holiday holiday;
        Long id = holidayDTO.getEmployeeId();
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty())
            throw new RegisterException("The employee entity does not exist with id: " + id + "!");
        else {
            Employee employee = employeeOptional.get();
            compareStartDateToBeginningDate(holidayDTO, employee);

            Long sumBusinessDay = searchBusinessDay.checkHolidayDateInterval(holidayDTO, null, null);

            System.out.println(sumBusinessDay);
            checkToSumBusinessDay(sumBusinessDay, employee);
            employee.setSumHoliday(sumBusinessDay);
            employeeRepository.save(employee);
            holiday = modelMapper.map(holidayDTO, Holiday.class);
            holiday.getLocalDateStorage().put(holidayDTO.getStartDate(), holidayDTO.getFinishDate());
            holiday.setEmployee(employee);
        }
        return holiday;
    }

    private void checkToSumBusinessDay(Long sumBusinessDay, Employee employee) {
        if (sumBusinessDay > (employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday()))
            throw new RegisterException("The number of holidays available is less than the requested leave! You have only " + (employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday()) + " days!");
    }

    private void compareStartDateToBeginningDate(HolidayDTO holidayDTO, Employee employee) {
        if (holidayDTO.getStartDate().isBefore(employee.getBeginningOfEmployment())) {
            throw new RegisterException("The beginning of employment must be earlier than the start date!");
        }
    }

    private void checkToStartDate(HolidayDTO holidayDTO) {
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getFinishDate()))
            throw new RegisterException("The start date must be earlier than the finish date!");
    }
}
