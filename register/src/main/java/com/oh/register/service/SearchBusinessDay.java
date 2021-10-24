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
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
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

    public Long checkHolidayDateInterval(HolidayDTO holidayDTO, Integer year, Month month) {
        LocalDate startDate = holidayDTO.getStartDate();
        LocalDate finishDate = holidayDTO.getFinishDate();
        if (year == null)
            year = startDate.getYear();


        Employee employee = employeeService.findById(holidayDTO.getEmployeeId());
        Integer maxHolidayOfYear = employee.getBasicLeave();
        Long sumHoliday = employee.getSumHoliday();

        List<Holiday> holidayList = holidayRepository.findAll();
        Holiday holiday;
        if (holidayList.size() == 0) {
            return getBusinessDay(holidayDTO.getStartDate(), holidayDTO.getFinishDate(), year);
        } else {
            holiday = holidayList.get(holidayList.size() - 1);

            Map<LocalDate, LocalDate> localDateStorage = holiday.getLocalDateStorage();


            Integer finalYear = year;
            AtomicReference<Long> sumBusinessDay = new AtomicReference<>(0L);
            localDateStorage.entrySet().stream()
                    .filter(entry -> entry.getKey().getYear() == finalYear)
                    .forEach(entry -> sumBusinessDay.updateAndGet(e -> e + getBusinessDay(startDate, finishDate, finalYear)));

            return sumBusinessDay.get();
        }
    }

    private Long getBusinessDay(LocalDate startDate, LocalDate finishDate, Integer year) {

        HolidayDay holidayDay = holidayDayRepository.findByYear(String.valueOf(year));
        if(holidayDay==null) throw new RegisterException("You do not have holiday day database!");
        List<LocalDate> localDateList = holidayDay.getLocalDate();

        Predicate<LocalDate> isHoliday = date -> localDateList != null && localDateList.contains(date);

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        long daysBetween = ChronoUnit.DAYS.between(startDate, finishDate);

        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysBetween)
                .filter(isHoliday.or(isWeekend).negate())
                .count();
    }
}
