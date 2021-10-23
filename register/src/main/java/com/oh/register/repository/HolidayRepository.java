package com.oh.register.repository;

import com.oh.register.model.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday,Long> {
  Holiday findByStartDate(LocalDate startDate);
  Holiday findByFinishDate(LocalDate finishDate);
}
