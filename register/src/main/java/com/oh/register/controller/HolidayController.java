package com.oh.register.controller;

import com.oh.register.config.BindingErrorHandler;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Operations on Holiday")
public class HolidayController {
    private final HolidayService holidayService;
    private final BindingErrorHandler bindingErrorHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public HolidayController(HolidayService holidayService, BindingErrorHandler bindingErrorHandler) {
        this.holidayService = holidayService;
        this.bindingErrorHandler = bindingErrorHandler;
    }

    @PostMapping("/holiday/employee/{id}/add")
    @Operation(summary = "save holiday", description = "save holiday")
    public ResponseEntity<HolidayDTO> save(@Valid @RequestBody HolidayDTO holidayDTO, BindingResult bindingResult, @PathVariable("id") Long id) {
        if (holidayDTO.getId() != null) holidayDTO.setId(null);
        String logMessage = "Posted holiday entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        holidayDTO.setEmployeeId(id);
        return ResponseEntity.ok(holidayService.save(holidayDTO));
    }

    @DeleteMapping("/holiday/employee/{id}/delete")
    @Operation(summary = "delete holiday", description = "delete holiday")
    public ResponseEntity<String> delete(@RequestBody HolidayDTO holidayDTO, @PathVariable("id") Long id) {
        holidayDTO.setEmployeeId(id);
        holidayService.delete(holidayDTO);
        return ResponseEntity.ok("The holiday was deleted from " + holidayDTO.getStartDate().toString() + " to " + holidayDTO.getFinishDate().toString() + "!");
    }

    @GetMapping("/businessday/employee/{id}/get-dateinterval")
    @Operation(summary = "get business days by date interval", description = "get business days by date interval")
    public ResponseEntity<String> getAllBusinessDayByDateInterval(@RequestParam("start") String startDate, @RequestParam("end") String endDate, @PathVariable("id") Long id) {
        if (id == null) throw new RegisterException("The given id mustn't be null!");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start, end;
        try {
            start = LocalDate.parse(startDate, formatter);
            end = LocalDate.parse(endDate, formatter);
        } catch (Exception ex) {
            throw new RegisterException("Invalid date format!");
        }
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setStartDate(start);
        holidayDTO.setFinishDate(end);
        holidayDTO.setEmployeeId(id);
        Long allBusinessDayByDateInterval = holidayService.findAllBusinessDayByDateInterval(holidayDTO);
        String response = String.format("The employee with id:%d worked %d days from %s to %s!", id, allBusinessDayByDateInterval, holidayDTO.getStartDate().toString(), holidayDTO.getFinishDate().toString());
        return ResponseEntity.ok(response);
    }

}
