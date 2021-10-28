package com.oh.register.controller;

import com.oh.register.exception.RegisterException;
import com.oh.register.model.dto.ChildrenDTO;
import com.oh.register.model.dto.EmployeeDTO;
import com.oh.register.model.dto.HolidayDTO;
import com.oh.register.model.dto.HolidayDayDTO;
import com.oh.register.model.entity.Employee;
import com.oh.register.service.HolidayService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class HolidayControllerITTest {

    @LocalServerPort
    private Integer port;

    private String BASE_URL;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private HolidayController holidayController;

    @Autowired
    private HolidayService holidayService;

    private List<ChildrenDTO> childrenDTOS = new ArrayList<>();

    private List<HolidayDTO> holidayDTOS = new ArrayList<>();

    private List<HolidayDayDTO> holidayDayDTOS = new ArrayList<>();

    private EmployeeDTO employeeDTO;

    @BeforeEach
    public void init() {
        BASE_URL = "http://localhost:" + port;

        HolidayDayDTO holidayDayDTO1 = new HolidayDayDTO();
        holidayDayDTO1.setId(1L);
        holidayDayDTO1.setYear("2021");
        holidayDayDTO1.setLocalDate(Arrays.asList(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 3, 15), LocalDate.of(2021, 4, 2)));

        HolidayDayDTO holidayDayDTO2 = new HolidayDayDTO();
        holidayDayDTO2.setId(2L);
        holidayDayDTO2.setYear("2022");
        holidayDayDTO2.setLocalDate(Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 3, 15), LocalDate.of(2022, 4, 2)));

        holidayDayDTOS.clear();
        holidayDayDTOS.addAll(Arrays.asList(holidayDayDTO1, holidayDayDTO2));

        for (HolidayDayDTO holidayDayDTO : holidayDayDTOS) {
            testRestTemplate.postForEntity(BASE_URL + "/holidayday/add", new HttpEntity<>(holidayDayDTO), HolidayDayDTO.class);
        }


        employeeDTO = new EmployeeDTO();
        employeeDTO.setBirthDate(LocalDate.of(1995, 1, 1));
        employeeDTO.setBeginningOfEmployment(LocalDate.of(2021, 1, 5));
        employeeDTO.setDateOfEntry(LocalDate.of(2021, 1, 2));
        employeeDTO.setFirstName("Kovács");
        employeeDTO.setLastName("Péter");
        employeeDTO.setIdentityNumber("111111111");
        employeeDTO.setPosition("manager");
        employeeDTO.setWorkplace("IBM");
        employeeDTO.setId(1L);

        testRestTemplate.postForObject(BASE_URL + "/employee/add", new HttpEntity<>(employeeDTO), Employee.class);

        ChildrenDTO children1 = new ChildrenDTO();
        children1.setId(1L);
        children1.setFirstName("Kovács");
        children1.setLastName("Péter");
        children1.setBirthDay(LocalDate.of(2020, 1, 1));

        ChildrenDTO children2 = new ChildrenDTO();
        children2.setId(2L);
        children2.setFirstName("Kovács");
        children2.setLastName("Ilona");
        children2.setBirthDay(LocalDate.of(2018, 1, 1));

        childrenDTOS.clear();
        childrenDTOS.addAll(Arrays.asList(children1, children2));
        long id = 1;
        for (ChildrenDTO children : childrenDTOS) {
            testRestTemplate.postForObject(BASE_URL + "/children/employee/{id}/add", new HttpEntity<>(children), ChildrenDTO.class, id);
        }

        HolidayDTO holidayDTO1 = new HolidayDTO();
        holidayDTO1.setId(1L);
        holidayDTO1.setEmployeeId(1L);
        holidayDTO1.setStartDate(LocalDate.of(2021, 2, 5));
        holidayDTO1.setFinishDate(LocalDate.of(2021, 2, 15));

        HolidayDTO holidayDTO2 = new HolidayDTO();
        holidayDTO2.setId(2L);
        holidayDTO2.setEmployeeId(1L);
        holidayDTO2.setStartDate(LocalDate.of(2021, 2, 25));
        holidayDTO2.setFinishDate(LocalDate.of(2021, 3, 5));

        HolidayDTO holidayDTO3 = new HolidayDTO();
        holidayDTO3.setId(3L);
        holidayDTO3.setEmployeeId(1L);
        holidayDTO3.setStartDate(LocalDate.of(2021, 12, 25));
        holidayDTO3.setFinishDate(LocalDate.of(2022, 1, 5));

        HolidayDTO holidayDTO4 = new HolidayDTO();
        holidayDTO4.setId(4L);
        holidayDTO4.setEmployeeId(1L);
        holidayDTO4.setStartDate(LocalDate.of(2021, 4, 30));
        holidayDTO4.setFinishDate(LocalDate.of(2021, 5, 5));

        holidayDTOS.clear();
        holidayDTOS.addAll(Arrays.asList(holidayDTO1, holidayDTO2, holidayDTO3, holidayDTO4));
        for (HolidayDTO holidayDTO : holidayDTOS) {
            testRestTemplate.postForEntity(BASE_URL + "/holiday/employee/{id}/add", new HttpEntity<>(holidayDTO), HolidayDTO.class, id);
        }
    }

    @Test
    void save_inputHolidayDTO_shouldReturnRightHolidayDTO() {
        long id = 1;
        HolidayDTO expected = new HolidayDTO();
        expected.setId(5L);
        expected.setStartDate(LocalDate.of(2021, 6, 25));
        expected.setFinishDate(LocalDate.of(2021, 6, 28));

        ResponseEntity<HolidayDTO> response = testRestTemplate.postForEntity(BASE_URL + "/holiday/employee/{id}/add", new HttpEntity<>(expected), HolidayDTO.class, id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HolidayDTO actual = response.getBody();
        assertEquals(expected, actual);
    }

    @Test
    void delete_inputHolidayDTOWithValidDateInterval_shouldReturnRightMessageAndLeaveRestored() {
        long id = 1;
        ResponseEntity<Employee> response;
        response = testRestTemplate.getForEntity(BASE_URL + "/employee/{id}/get", Employee.class, id);
        long sumHoliday = Objects.requireNonNull(response.getBody()).getSumHoliday();
        long expectedHoliday = 23;
        assertEquals(expectedHoliday, sumHoliday);
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(2L);
        holidayDTO.setEmployeeId(1L);
        holidayDTO.setStartDate(LocalDate.of(2021, 2, 25));
        holidayDTO.setFinishDate(LocalDate.of(2021, 3, 5));
        ResponseEntity<String> responseAfterDelete = holidayController.delete(holidayDTO, 1L);

        String expected = "The holiday was deleted from " + holidayDTO.getStartDate().toString() + " to " + holidayDTO.getFinishDate().toString() + "!";
        String actual = responseAfterDelete.getBody();
        assertEquals(HttpStatus.OK, responseAfterDelete.getStatusCode());
        assertEquals(expected, actual);

        long expectedHolidayAfterDelete = 16;
        response = testRestTemplate.getForEntity(BASE_URL + "/employee/{id}/get", Employee.class, id);
        assertEquals(expectedHolidayAfterDelete, Objects.requireNonNull(response.getBody()).getSumHoliday());
    }

    @Test
    void delete_inputHolidayDTOWithBadDateInterval_shouldReturnRegisterException() {
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(2L);
        holidayDTO.setEmployeeId(1L);
        holidayDTO.setStartDate(LocalDate.of(2021, 2, 25));
        holidayDTO.setFinishDate(LocalDate.of(2021, 3, 3));
        String actual = "";
        try {
            holidayController.delete(holidayDTO, 1L);
        } catch (RegisterException ex) {
            actual = ex.getMessage();
        }
        String expected = "The specified date interval doesn't exist for the employee with id:1!";
        assertEquals(expected, actual);
    }


    @Test
    void delete_inputHolidayDTOWithFinishDateIsEarlierThanStartDate_shouldReturnRegisterException() {
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(2L);
        holidayDTO.setEmployeeId(1L);
        holidayDTO.setStartDate(LocalDate.of(2021, 3, 6));
        holidayDTO.setFinishDate(LocalDate.of(2021, 3, 3));
        String actual = "";
        try {
            holidayController.delete(holidayDTO, 1L);
        } catch (RegisterException ex) {
            actual = ex.getMessage();
        }
        String expected = "The start date must be earlier than the finish date!";
        assertEquals(expected, actual);
    }


    @Test
    void delete_inputHolidayDTOWithStartDateIsEarlierThanBeginningDateOfEmployeeDTO_shouldReturnRegisterException() {
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(2L);
        holidayDTO.setEmployeeId(1L);
        holidayDTO.setStartDate(LocalDate.of(2021, 1, 2));
        holidayDTO.setFinishDate(LocalDate.of(2021, 2, 15));
        String actual = "";
        try {
            holidayController.delete(holidayDTO, 1L);
        } catch (RegisterException ex) {
            actual = ex.getMessage();
        }
        String expected = "The beginning of employment must be earlier than the start date!";
        assertEquals(expected, actual);
    }

    @Test
    void delete_inputHolidayDTOAndDeleteBeginningDateOfEmployeeDTO_shouldReturnRegisterException() {
        employeeDTO.setBeginningOfEmployment(null);
        testRestTemplate.put(BASE_URL + "/employee/{id}/update", new HttpEntity<>(employeeDTO), 1);
        HolidayDTO holidayDTO = new HolidayDTO();
        holidayDTO.setId(2L);
        holidayDTO.setEmployeeId(1L);
        holidayDTO.setStartDate(LocalDate.of(2021, 4, 30));
        holidayDTO.setFinishDate(LocalDate.of(2021, 5, 5));
        String actual = "";
        try {
            holidayController.delete(holidayDTO, 1L);
        } catch (RegisterException ex) {
            actual = ex.getMessage();
        }
        String expected = "The employee doesn't have beginning date!";
        assertEquals(expected, actual);
    }

    @Test
    void getAllBusinessDayByDateInterval() {
    }

    @Test
    void getAllBusinessDayByYearAndMonth() {
    }

    @Test
    void getHolidayByDateInterval() {
    }

    @Test
    void getNumberOfHolidayByDateInterval() {
    }
}