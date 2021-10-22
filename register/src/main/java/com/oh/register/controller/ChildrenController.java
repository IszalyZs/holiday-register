package com.oh.register.controller;

import com.oh.register.service.ChildrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class ChildrenController {

    private final ChildrenService childrenService;

    @Autowired
    public ChildrenController(ChildrenService childrenService) {
        this.childrenService = childrenService;
    }
}
