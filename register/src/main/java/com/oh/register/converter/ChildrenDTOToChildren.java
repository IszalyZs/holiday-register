package com.oh.register.converter;

import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.ChildrenDTO;
import com.oh.register.model.entity.Children;
import com.oh.register.model.entity.Employee;
import com.oh.register.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChildrenDTOToChildren {
    private final ModelMapper modelMapper;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ChildrenDTOToChildren(ModelMapper modelMapper, EmployeeRepository employeeRepository) {
        this.modelMapper = modelMapper;
        this.employeeRepository = employeeRepository;
    }

    public Children getChildren(ChildrenDTO childrenDTO) {
        Children children;
        Long id = childrenDTO.getEmployeeId();
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty())
            throw new RegisterException("The employee entity does not exist with id: " + id + "!");
        else {
            children = modelMapper.map(childrenDTO, Children.class);
            children.setEmployee(employeeOptional.get());
        }
        return children;
    }
}
