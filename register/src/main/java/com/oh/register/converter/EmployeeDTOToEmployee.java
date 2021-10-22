package com.oh.register.converter;

import com.oh.register.model.dto.EmployeeDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.service.ChildrenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployeeDTOToEmployee {
    private final ModelMapper modelMapper;
    private final ChildrenService childrenService;

    @Autowired
    public EmployeeDTOToEmployee(ModelMapper modelMapper, ChildrenService childrenService) {
        this.modelMapper = modelMapper;
        this.childrenService = childrenService;
    }

    public Employee getEmployee(EmployeeDTO employeeDTO) {
        return modelMapper.map(employeeDTO, Employee.class);
    }
}
