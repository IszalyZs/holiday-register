package com.oh.register.controller;

import com.oh.register.config.BindingErrorHandler;
import com.oh.register.model.dto.ChildrenDTO;
import com.oh.register.service.ChildrenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/children")
@Tag(name = "Operations on Children")
public class ChildrenController {

    private final ChildrenService childrenService;
    private final BindingErrorHandler bindingErrorHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ChildrenController(ChildrenService childrenService, BindingErrorHandler bindingErrorHandler) {
        this.childrenService = childrenService;
        this.bindingErrorHandler = bindingErrorHandler;
    }

    @GetMapping
    @Operation(summary = "list all children", description = "list all children")
    public ResponseEntity<List<ChildrenDTO>> findAll() {
        return ResponseEntity.ok(childrenService.findAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete children by id", description = "delete children by id")
    public ResponseEntity<String> deleteById(@PathVariable("id") Long id) {
        childrenService.deleteById(id);
        return ResponseEntity.ok("The entity was deleted with id: " + id + "!");
    }

    @GetMapping("/{id}")
    @Operation(summary = "list children by id", description = "list children by id")
    public ResponseEntity<ChildrenDTO> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(childrenService.findById(id));
    }

    @PostMapping
    @Operation(summary = "save children", description = "save children")
    public ResponseEntity<?> save(@Valid @RequestBody ChildrenDTO childrenDTO, BindingResult bindingResult) {
        if (childrenDTO.getId() != null) childrenDTO.setId(null);
        String logMessage = "Posted children entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        return ResponseEntity.ok(childrenService.save(childrenDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "update children by id", description = "update children by id")
    public ResponseEntity<?> update(@Valid @RequestBody ChildrenDTO childrenDTO, BindingResult bindingResult, @PathVariable("id") Long id) {
        String logMessage = "Updated children entity contains error(s): ";
        bindingErrorHandler.bindingResult(bindingResult, logMessage, logger);
        childrenDTO.setId(id);
        return ResponseEntity.ok(childrenService.update(childrenDTO));
    }


}
