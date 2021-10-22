package com.oh.register.service;

import com.oh.register.converter.EmployeeDTOToEmployee;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.EmployeeDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeDTOToEmployee employeeDTOToEmployee;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeDTOToEmployee employeeDTOToEmployee) {
        this.employeeRepository = employeeRepository;
        this.employeeDTOToEmployee = employeeDTOToEmployee;
    }

    public List<Employee> findAll() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.size() == 0) {
            throw new RegisterException("The employee entities do not exist!");
        }
        return employees;
    }

    public void deleteById(Long id) {
        try {
            employeeRepository.deleteById(id);
        } catch (Exception exception) {
            throw new RegisterException("No employee entity with id: " + id + "!");
        }
    }

    public Employee findById(Long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            return employeeOptional.get();
        }
        throw new RegisterException("The employee entity does not exist with id: " + id + "!");
    }

    public Employee save(EmployeeDTO employeeDTO) {
        return employeeRepository.save(employeeDTOToEmployee.getEmployee(employeeDTO));
    }

    public Employee update(EmployeeDTO employeeDTO) {
        return employeeRepository.save(employeeDTOToEmployee.getEmployee(employeeDTO));
    }
}
