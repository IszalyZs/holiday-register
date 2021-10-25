package com.oh.register.service;

import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class SearchBusinessDay {
    private final HolidayDayRepository holidayDayRepository;
    private final HolidayRepository holidayRepository;

    @Autowired
    public SearchBusinessDay(HolidayDayRepository holidayDayRepository, HolidayRepository holidayRepository) {
        this.holidayDayRepository = holidayDayRepository;
        this.holidayRepository = holidayRepository;
    }

    public Long getBusinessDay(HolidayDTO holidayDTO, Integer year, Month month) {
        LocalDate startDate = holidayDTO.getStartDate();
        if (year == null)
            year = startDate.getYear();

        List<Holiday> holidayList = holidayRepository.findAll();
        if (holidayList.size() == 0)
            return getSumBusinessDay(holidayDTO.getStartDate(), holidayDTO.getFinishDate(), year);

        Integer finalYear = year;
        AtomicReference<Long> sumBusinessDay = new AtomicReference<>(0L);
        holidayList.stream()
                .filter(holiday -> holiday.getStartDate().getYear() == finalYear)
                .forEach(item -> sumBusinessDay.updateAndGet(e -> e + getSumBusinessDay(item.getStartDate(), item.getFinishDate(), finalYear)));
        return sumBusinessDay.get();
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
