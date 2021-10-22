package com.oh.register.service;

import com.oh.register.repository.ChildrenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChildrenService {

    private final ChildrenRepository childrenRepository;

    @Autowired
    public ChildrenService(ChildrenRepository childrenRepository) {
        this.childrenRepository = childrenRepository;
    }
}
