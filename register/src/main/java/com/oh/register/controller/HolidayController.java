package com.oh.register.controller;

import com.oh.register.config.BindingErrorHandler;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.service.HolidayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/holiday")
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

    @GetMapping
    @Operation(summary = "list all holidays", description = "list all holidays")
    public ResponseEntity<List<HolidayDTO>> findAll() {
        return ResponseEntity.ok(holidayService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete holiday by id", description = "delete holiday by id")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        holidayService.deleteById(id);
        return ResponseEntity.ok("The entity was deleted with id: " + id + "!");
    }

    @GetMapping("/{id}")
    @Operation(summary = "list holiday by id", description = "list holiday by id")
    public ResponseEntity<HolidayDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(holidayService.findById(id));
    }

    @PostMapping
    @Operation(summary = "save holiday", description = "save holiday")
    public ResponseEntity<?> save(@Valid @RequestBody HolidayDTO holidayDTO, BindingResult bindingResult) {
        if (holidayDTO.getId() != null) holidayDTO.setId(null);
        HolidayDTO response;
        String logMessage = "Posted holiday entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        try {
            response = holidayService.save(holidayDTO);
        } catch (DataIntegrityViolationException exception) {
            String message = "Duplicate entry at holiday's year:" + holidayDTO.getYear() + " is already exists!";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update holiday by id", description = "update holiday by id")
    public ResponseEntity<?> update(@Valid @RequestBody HolidayDTO holidayDTO, BindingResult bindingResult, @PathVariable("id") Long id) {
        HolidayDTO response;
        String logMessage = "Updated holiday entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        try {
            holidayDTO.setId(id);
            response = holidayService.update(holidayDTO);
        } catch (DataIntegrityViolationException exception) {
            String message = "Duplicate entry at holiday's year:" + holidayDTO.getYear() + " is already exists!";
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(response);
    }

}
