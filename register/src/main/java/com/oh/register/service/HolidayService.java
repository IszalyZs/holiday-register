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

import java.util.Optional;

@Service
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayDTOTOHoliday holidayDTOTOHoliday;
    private final HolidayToHolidayDTO holidayToHolidayDTO;
    private final EmployeeRepository employeeRepository;
    private final SearchBusinessDay searchBusinessDay;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, HolidayDTOTOHoliday holidayDTOTOHoliday, HolidayToHolidayDTO holidayToHolidayDTO, EmployeeRepository employeeRepository, SearchBusinessDay searchBusinessDay) {
        this.holidayRepository = holidayRepository;
        this.holidayDTOTOHoliday = holidayDTOTOHoliday;
        this.holidayToHolidayDTO = holidayToHolidayDTO;
        this.employeeRepository = employeeRepository;
        this.searchBusinessDay = searchBusinessDay;
    }

    public HolidayDTO save(HolidayDTO holidayDTO) {
        checkToStartDate(holidayDTO);
        Holiday holiday;
        Long sumBusinessDay;
        Employee employee = getEmployeeById(holidayDTO);

        compareStartDateToBeginningDate(holidayDTO, employee);

        sumBusinessDay = searchBusinessDay.checkHolidayDateInterval(holidayDTO, null, null);

        checkToSumBusinessDay(sumBusinessDay, employee);

        holiday = holidayDTOTOHoliday.getHoliday(holidayDTO);
        holiday.getLocalDateStorage().put(holidayDTO.getStartDate(), holidayDTO.getFinishDate());
        holiday.setEmployee(employee);
        Holiday savedHoliday = holidayRepository.save(holiday);

        saveChangedEmployee(savedHoliday, sumBusinessDay);
        return holidayToHolidayDTO.getHolidayDTO(savedHoliday);
    }


    public void delete(HolidayDTO holidayDTO) {
        checkToStartDate(holidayDTO);
        Employee employee = getEmployeeById(holidayDTO);
        compareStartDateToBeginningDate(holidayDTO, employee);

        Holiday holiday = holidayDTOTOHoliday.getHoliday(holidayDTO);
        Holiday holidayByStartDate = holidayRepository.findByStartDate(holiday.getStartDate());
        Holiday holidayByFinishDate = holidayRepository.findByFinishDate(holiday.getFinishDate());

        if (holidayByStartDate.equals(holidayByFinishDate)) {
            Long id = holidayByStartDate.getId();
            holidayRepository.deleteById(id);
        } else throw new RegisterException("The specified interval does not exist!");
    }


    private Employee getEmployeeById(HolidayDTO holidayDTO) {
        Long id = holidayDTO.getEmployeeId();

        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty())
            throw new RegisterException("The employee entity does not exist with id: " + id + "!");

        return employeeOptional.get();
    }


    private void saveChangedEmployee(Holiday savedHoliday, Long sumBusinessDay) {
        Employee employee = savedHoliday.getEmployee();
        employee.setHoliday(savedHoliday);
        employee.setSumHoliday(employee.getSumHoliday() + sumBusinessDay);
        employeeRepository.save(employee);
    }

    private void checkToSumBusinessDay(Long sumBusinessDay, Employee employee) {
        if (sumBusinessDay > (employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday()))
            throw new RegisterException("The number of holidays available is less than the requested leave! You have only " + (employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday()) + " days!");
    }

    private void compareStartDateToBeginningDate(HolidayDTO holidayDTO, Employee employee) {
        if (employee.getBeginningOfEmployment() == null)
            throw new RegisterException("The employee doesn't have beginning date!");
        else if (holidayDTO.getStartDate().isBefore(employee.getBeginningOfEmployment())) {
            throw new RegisterException("The beginning of employment must be earlier than the start date!");
        }
    }

    private void checkToStartDate(HolidayDTO holidayDTO) {
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getFinishDate()))
            throw new RegisterException("The start date must be earlier than the finish date!");
    }
}
