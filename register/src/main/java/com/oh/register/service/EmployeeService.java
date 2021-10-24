package com.oh.register.service;

import com.oh.register.converter.EmployeeDTOToEmployee;
import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.EmployeeDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        checkToBeginningDate(employeeDTO.getBeginningOfEmployment(), employeeDTO.getDateOfEntry());
        return employeeRepository.save(setBasicLeaveToEmployee(employeeDTO));
    }


    public Employee update(EmployeeDTO employeeDTO) {
        checkToBeginningDate(employeeDTO.getBeginningOfEmployment(), employeeDTO.getDateOfEntry());
        return employeeRepository.save(setBasicLeaveToEmployee(employeeDTO));
    }

    public void saveWithEmployee(Employee employee) {
        checkToBeginningDate(employee.getBeginningOfEmployment(), employee.getDateOfEntry());
        employee.setBasicLeave();
        employeeRepository.save(employee);
    }


    private Employee setBasicLeaveToEmployee(EmployeeDTO employeeDTO) {
        Employee employee = employeeDTOToEmployee.getEmployee(employeeDTO);
        employee.setBasicLeave();
        return employee;
    }


    private void checkToBeginningDate(LocalDate beginningDate, LocalDate dateOfEntry) {
        if (beginningDate != null && beginningDate.isBefore(dateOfEntry))
            throw new RegisterException("The date of entry must be earlier than the beginning of employment!");
    }

}
