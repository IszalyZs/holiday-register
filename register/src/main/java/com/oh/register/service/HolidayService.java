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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

        Employee employee = getEmployeeByHolidayDTO(holidayDTO);

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
        Employee employee = getEmployeeByHolidayDTO(holidayDTO);
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
        Employee employee = getEmployeeByHolidayDTO(holidayDTO);
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


    private Employee getEmployeeByHolidayDTO(HolidayDTO holidayDTO) {
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

    public Long findAllBusinessDayByDateInterval(HolidayDTO holidayDTO) {
        checkingBeginningOfEmploymentDate(holidayDTO);
        LocalDate startDate;
        Long sumBusinessDay = 0L;
        if (getEmployeeByHolidayDTO(holidayDTO).getBeginningOfEmployment().isAfter(holidayDTO.getStartDate()))
            startDate = getEmployeeByHolidayDTO(holidayDTO).getBeginningOfEmployment();
        else startDate = holidayDTO.getStartDate();

        AtomicReference<Long> sumHoliday = new AtomicReference<>(0L);
        if (holidayDTO.getStartDate().getYear() == holidayDTO.getFinishDate().getYear()) {
            sumBusinessDay = searchBusinessDay.getSumBusinessDay(startDate, holidayDTO.getFinishDate(), holidayDTO.getFinishDate().getYear());
            List<Holiday> holidays = holidayRepository.findByEmployee_Id(holidayDTO.getEmployeeId());
            holidays.forEach(holiday -> sumHoliday.updateAndGet(v -> v + searchHoliday(holiday, holidayDTO)));
        } else {
            int startYear = holidayDTO.getStartDate().getYear();
            int finishYear = holidayDTO.getFinishDate().getYear();
            List<Integer> collect = IntStream.rangeClosed(startYear, finishYear).boxed().collect(Collectors.toList());
            LocalDate start = startDate;
            for (int i = 0; i < collect.size(); i++) {
                LocalDate finish = LocalDate.of(collect.get(i), 12, 31);
                if (i != 0)
                    start = LocalDate.of(collect.get(i), 1, 1);
                if (i == collect.size() - 1)
                    finish = holidayDTO.getFinishDate();
                sumBusinessDay += searchBusinessDay.getSumBusinessDay(start, finish, collect.get(i));
            }
            List<Holiday> holidays = holidayRepository.findByEmployee_Id(holidayDTO.getEmployeeId());

            holidays.forEach(holiday -> sumHoliday.updateAndGet(v -> v + searchHoliday(holiday, holidayDTO)));
        }
        return sumBusinessDay - sumHoliday.get();
    }

    private void checkingBeginningOfEmploymentDate(HolidayDTO holidayDTO) {
        if (getEmployeeByHolidayDTO(holidayDTO).getBeginningOfEmployment() == null)
            throw new RegisterException("The beginning of employment date is null!");
    }

    private Long searchHoliday(Holiday holiday, HolidayDTO holidayDTO) {
        LocalDate startDate = holidayDTO.getStartDate();
        LocalDate endDate = LocalDate.of(holidayDTO.getFinishDate().getYear(), holidayDTO.getFinishDate().getMonth().getValue(), holidayDTO.getFinishDate().getDayOfMonth());
        endDate = endDate.plusDays(1);
        List<LocalDate> collect = startDate.datesUntil(endDate)
                .collect(Collectors.toList());

        Predicate<LocalDate> isSegment = date -> collect.contains(date);

        Predicate<LocalDate> isBusinessDay = date -> searchBusinessDay.isBusinessDay(date);

        long daysBetween = ChronoUnit.DAYS.between(holiday.getStartDate(), holiday.getFinishDate());

        return Stream.iterate(holiday.getStartDate(), date -> date.plusDays(1))
                .limit(daysBetween + 1)
                .filter(isSegment.and(isBusinessDay))
                .count();
    }


}
