package com.oh.register.controller;

import com.oh.register.config.BindingErrorHandler;
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

@RestController
@CrossOrigin(origins = "*")
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

    @PostMapping("/employee/{id}/add")
    @Operation(summary = "save holiday", description = "save holiday")
    public ResponseEntity<HolidayDTO> save(@Valid @RequestBody HolidayDTO holidayDTO, BindingResult bindingResult,@PathVariable("id") Long id) {
        if (holidayDTO.getId() != null) holidayDTO.setId(null);
        String logMessage = "Posted holiday entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        holidayDTO.setEmployeeId(id);
        return ResponseEntity.ok(holidayService.save(holidayDTO));
    }

    @DeleteMapping("/employee/{id}/delete")
    @Operation(summary = "delete holiday", description = "delete holiday")
    public ResponseEntity<String> delete(@RequestBody HolidayDTO holidayDTO,@PathVariable("id") Long id){
        holidayDTO.setEmployeeId(id);
        holidayService.delete(holidayDTO);
        return ResponseEntity.ok("The holiday was deleted from "+holidayDTO.getStartDate().toString()+" to "+holidayDTO.getFinishDate().toString()+"!");

    }

}
