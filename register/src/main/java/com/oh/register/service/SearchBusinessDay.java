package com.oh.register.service;

import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.model.entity.Holiday;
import com.oh.register.model.entity.HolidayDay;
import com.oh.register.repository.HolidayDayRepository;
import com.oh.register.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class SearchBusinessDay {
    private final HolidayDayRepository holidayDayRepository;
    private final HolidayRepository holidayRepository;
    private final EmployeeService employeeService;

    @Autowired
    public SearchBusinessDay(HolidayDayRepository holidayDayRepository, HolidayRepository holidayRepository, EmployeeService employeeService) {
        this.holidayDayRepository = holidayDayRepository;
        this.holidayRepository = holidayRepository;
        this.employeeService = employeeService;
    }

    public Long getBusinessDay(HolidayDTO holidayDTO, Integer year, Month month) {
        if (year == null)
            year = holidayDTO.getStartDate().getYear();

        checkingDateInterval(holidayDTO);

        if ((holidayDTO.getStartDate().getYear() == holidayDTO.getFinishDate().getYear()) && holidayDTO.getFinishDate().getYear() == LocalDate.now().getYear())
            return getSumBusinessDay(holidayDTO.getStartDate(), holidayDTO.getFinishDate(), year);
        else if ((holidayDTO.getStartDate().getYear() != holidayDTO.getFinishDate().getYear()) && holidayDTO.getFinishDate().getYear() == (LocalDate.now().getYear() + 1)) {
            LocalDate localDate = LocalDate.of(holidayDTO.getStartDate().getYear(), 12, 31);
            Long sumBusinessDayThisYear = getSumBusinessDay(holidayDTO.getStartDate(), localDate, holidayDTO.getStartDate().getYear());
            Long sumBusinessDayNextYear = getSumBusinessDay(LocalDate.of(holidayDTO.getFinishDate().getYear(), 01, 01), holidayDTO.getFinishDate(), holidayDTO.getFinishDate().getYear());
            checkingThisYearNumberOfHoliday(sumBusinessDayThisYear, holidayDTO);
            checkingNextYearNumberOfHoliday(sumBusinessDayNextYear, holidayDTO);
            return sumBusinessDayThisYear;
        } else throw new RegisterException("Invalid date interval!");
    }

    private void checkingThisYearNumberOfHoliday(Long sumBusinessDayThisYear, HolidayDTO holidayDTO) {
        Employee employee = employeeService.findById(holidayDTO.getEmployeeId());
        if ((employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday() - sumBusinessDayThisYear) <= 0)
            throw new RegisterException("The number of holidays available is less than the requested leave! You have only " + (employee.getBasicLeave() + employee.getExtraLeave() - employee.getSumHoliday()) + " days!");
    }

    private void checkingNextYearNumberOfHoliday(Long sumBusinessDayNextYear, HolidayDTO holidayDTO) {
        Employee employee = employeeService.findById(holidayDTO.getEmployeeId());
        if ((employee.getNextYearLeave() - employee.getSumHolidayNextYear() - sumBusinessDayNextYear) >= 0) {
            employee.setSumHolidayNextYear(employee.getSumHolidayNextYear() + sumBusinessDayNextYear);
            employeeService.saveEmployee(employee);
        } else
            throw new RegisterException("The number of holidays available is less than the requested leave! You have only " + (employee.getNextYearLeave() - employee.getSumHolidayNextYear()) + " days!");
    }

    private void checkingDateInterval(HolidayDTO holidayDTO) {

        List<Holiday> holidayList = holidayRepository.findAll();

        if (holidayList.size() != 0) {
            Optional<Holiday> holidayOptional = holidayList.stream()
                    .filter(date -> (date.getStartDate().isEqual(holidayDTO.getStartDate()) && date.getFinishDate().isEqual(holidayDTO.getFinishDate()) && date.getEmployee().getId() == holidayDTO.getEmployeeId()))
                    .findAny();
            if (holidayOptional.isPresent())
                throw new RegisterException("This date interval from " + holidayDTO.getStartDate() + " to " + holidayDTO.getFinishDate() + " already exists for the worker!");

            Optional<Holiday> optionalHoliday = holidayList.stream()
                    .filter(date -> ((date.getStartDate().isEqual(holidayDTO.getStartDate())
                            || date.getFinishDate().isEqual(holidayDTO.getFinishDate())
                            || date.getStartDate().isEqual(holidayDTO.getFinishDate())
                            || date.getFinishDate().isEqual(holidayDTO.getStartDate()))
                            && date.getEmployee().getId() == holidayDTO.getEmployeeId()))
                    .findAny();
            if (optionalHoliday.isPresent())
                throw new RegisterException("The start date or the finish date already exists for the worker!");

            holidayList.stream().forEach(date -> checkingDateOverlap(date, holidayDTO));
        }
    }

    private void checkingDateOverlap(Holiday date, HolidayDTO holidayDTO) {
        LocalDate startDate = date.getStartDate();
        LocalDate finishDate = date.getFinishDate();
        if (((holidayDTO.getStartDate().isAfter(startDate) && holidayDTO.getStartDate().isBefore(finishDate))
                || (holidayDTO.getFinishDate().isAfter(startDate) && holidayDTO.getFinishDate().isBefore(finishDate)))
                && date.getEmployee().getId() == holidayDTO.getEmployeeId())
            throw new RegisterException("The interval should not be overlap with an existing interval!");
    }

    private Long getSumBusinessDay(LocalDate startDate, LocalDate finishDate, Integer year) {

        HolidayDay holidayDay = holidayDayRepository.findByYear(String.valueOf(year));
        if (holidayDay == null) throw new RegisterException("You do not have holiday day database!");

        List<LocalDate> localDateList = holidayDay.getLocalDate();

        Predicate<LocalDate> isHoliday = date -> localDateList != null && localDateList.contains(date);

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, finishDate);

        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween + 1)
                .filter(isHoliday.or(isWeekend).negate())
                .count();
    }
}
