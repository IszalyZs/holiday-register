package com.oh.register.service;

import com.oh.register.converter.ChildrenDTOToChildren;
import com.oh.register.converter.ChildrenToChildrenDTO;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.ChildrenDTO;
import com.oh.register.model.entity.Children;
import com.oh.register.model.entity.Employee;
import com.oh.register.repository.ChildrenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChildrenService {

    private final ChildrenRepository childrenRepository;
    private final ChildrenDTOToChildren childrenDTOToChildren;
    private final ChildrenToChildrenDTO childrenToChildrenDTO;
    private final EmployeeService employeeService;

    @Autowired
    public ChildrenService(ChildrenRepository childrenRepository, ChildrenDTOToChildren childrenDTOToChildren, ChildrenToChildrenDTO childrenToChildrenDTO, EmployeeService employeeService) {
        this.childrenRepository = childrenRepository;
        this.childrenDTOToChildren = childrenDTOToChildren;
        this.childrenToChildrenDTO = childrenToChildrenDTO;
        this.employeeService = employeeService;
    }

    public List<ChildrenDTO> findAll() {
        List<Children> childrenList = childrenRepository.findAll();
        if (childrenList.size() == 0) {
            throw new RegisterException("The children entities do not exist!");
        }
        return childrenList.stream().map(childrenToChildrenDTO::getChildrenDTO).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        try {
            Long employeeId = this.findById(id).getEmployeeId();
            childrenRepository.deleteById(id);
            updateBasicLeave(null,employeeId);
        } catch (Exception exception) {
            throw new RegisterException("No children entity with id: " + id + "!");
        }
    }

    public ChildrenDTO findById(Long id) {
        Optional<Children> optionalChildren = childrenRepository.findById(id);
        if (optionalChildren.isPresent()) {
            Children children = optionalChildren.get();
            return childrenToChildrenDTO.getChildrenDTO(children);
        }
        throw new RegisterException("The children entity does not exist with id: " + id + "!");
    }

    public ChildrenDTO save(ChildrenDTO childrenDTO) {
        Children children = childrenRepository.save(childrenDTOToChildren.getChildren(childrenDTO));
        updateBasicLeave(childrenDTO,null);
        return childrenToChildrenDTO.getChildrenDTO(children);
    }

    public ChildrenDTO update(ChildrenDTO childrenDTO) {
        Children children = childrenRepository.save(childrenDTOToChildren.getChildren(childrenDTO));
        updateBasicLeave(childrenDTO,null);
        return childrenToChildrenDTO.getChildrenDTO(children);
    }

    private void updateBasicLeave(ChildrenDTO childrenDTO, Long employeeId) {
        List<Children> childrenList = childrenRepository.findAll();
        Employee employee;
        if (employeeId == null)
            employee = employeeService.findById(childrenDTO.getEmployeeId());
        else employee = employeeService.findById(employeeId);
        if (childrenList.size() == 1)
            employee.setExtraLeave(2L);
        else if (childrenList.size() == 2)
            employee.setExtraLeave(4L);
        else if (childrenList.size() > 2)
            employee.setExtraLeave(7L);
        else employee.setExtraLeave(0L);
        employeeService.saveWithEmployee(employee);
    }
}
