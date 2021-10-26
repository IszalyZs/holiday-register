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

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
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

    @Transactional
    public HolidayDTO save(HolidayDTO holidayDTO) {
        checkingStartDate(holidayDTO);

        Employee employee = getEmployeeById(holidayDTO);

        compareStartDateToBeginningDate(holidayDTO, employee);

        Long businessDays = searchBusinessDay.getBusinessDay(holidayDTO, null, null);//*******************

        checkingBusenessDays(businessDays, employee);

        Holiday holiday = holidayDTOTOHoliday.getHoliday(holidayDTO);

        Holiday savedHoliday = holidayRepository.save(holiday);

        Employee savedEmployee = savedHoliday.getEmployee();


        savedEmployee.setSumHoliday(savedEmployee.getSumHoliday() + businessDays);
        employeeRepository.save(savedEmployee);


        return holidayToHolidayDTO.getHolidayDTO(savedHoliday);
    }


    public void delete(HolidayDTO holidayDTO) {
        checkingStartDate(holidayDTO);
        Employee employee = getEmployeeById(holidayDTO);
        compareStartDateToBeginningDate(holidayDTO, employee);

        Long employeeId = holidayDTO.getEmployeeId();
        List<Holiday> holidays = holidayRepository.findByEmployee_Id(employeeId);
        Holiday foundHoliday = holidays.stream().filter(holiday -> (
                holiday.getStartDate().isEqual(holidayDTO.getStartDate()) &&
                        holiday.getFinishDate().isEqual(holidayDTO.getFinishDate()
                        ))).findAny().orElseThrow(() -> new RegisterException("The specified date interval doesn't exist for the employee with id:" + employeeId + "!"));

        Long id = foundHoliday.getId();
        try {
            holidayRepository.deleteById(id);
            addDeletedDaysToEmployee(holidayDTO);
        } catch (Exception ex) {
            throw new RegisterException("No row with id: " + id + "!");
        }
    }

    private void addDeletedDaysToEmployee(HolidayDTO holidayDTO) {
        Employee employee = getEmployeeById(holidayDTO);
        if (holidayDTO.getStartDate().getYear() == holidayDTO.getFinishDate().getYear()) {
            Long sumBusinessDayThisYear = searchBusinessDay.getSumBusinessDay(holidayDTO.getStartDate(), holidayDTO.getFinishDate(), holidayDTO.getStartDate().getYear());
            employee.setSumHoliday(employee.getSumHoliday() - sumBusinessDayThisYear);
        } else {
            Long sumBusinessDayThisYear = searchBusinessDay.getSumBusinessDay(holidayDTO.getStartDate(), LocalDate.of(holidayDTO.getStartDate().getYear(), 12, 31), holidayDTO.getStartDate().getYear());
            Long sumBusinessDayNextYear = searchBusinessDay.getSumBusinessDay(LocalDate.of(holidayDTO.getFinishDate().getYear(), 1, 1), holidayDTO.getFinishDate(), holidayDTO.getFinishDate().getYear());
            employee.setSumHoliday(employee.getSumHoliday() - sumBusinessDayThisYear);
            employee.setSumHolidayNextYear(employee.getSumHolidayNextYear() - sumBusinessDayNextYear);
        }
        employeeRepository.save(employee);
    }


    private Employee getEmployeeById(HolidayDTO holidayDTO) {
        Long id = holidayDTO.getEmployeeId();

        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty())
            throw new RegisterException("The employee entity doesn't exist with id: " + id + "!");

        return employeeOptional.get();
    }

    private void checkingBusenessDays(Long sumBusinessDay, Employee employee) {
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

    private void checkingStartDate(HolidayDTO holidayDTO) {
        if (holidayDTO.getStartDate().isAfter(holidayDTO.getFinishDate()))
            throw new RegisterException("The start date must be earlier than the finish date!");
    }
}
