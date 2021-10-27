package com.oh.register.controller;

import com.oh.register.model.dto.HolidayDayDTO;
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
class HolidayDayControllerITTest {

    @LocalServerPort
    private Integer port;

    private String BASE_URL;

    private List<HolidayDayDTO> holidayDayDTOS = new ArrayList<>();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void init() {
        BASE_URL = "http://localhost:" + port + "/holidayday";
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
            testRestTemplate.postForEntity(BASE_URL + "/add", new HttpEntity<>(holidayDayDTO), HolidayDayDTO.class);
        }
    }

    @Test
    void findAll_shouldReturnAllHolidayDayDTO() {
        ResponseEntity<HolidayDayDTO[]> response = testRestTemplate.getForEntity(BASE_URL + "/all", HolidayDayDTO[].class);
        List<HolidayDayDTO> actual = Arrays.asList(Objects.requireNonNull(response.getBody()));
        List<HolidayDayDTO> expected = holidayDayDTOS;
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, actual);
    }


    @Test
    void findById_inputValidId_shouldReturnRightHolidayDayDTO() {
        long id = 1;
        ResponseEntity<HolidayDayDTO> response = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", HolidayDayDTO.class, id);
        HolidayDayDTO expected = holidayDayDTOS.get(0);
        HolidayDayDTO actual = response.getBody();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, actual);
    }

    @Test
    void findById_inputBadId_shouldReturnBadRequest() {
        long badId = 3;
        ResponseEntity<String> response = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", String.class, badId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = "The holidayDay entity doesn't exist with id: 3!";
        assertEquals(expected, response.getBody());
    }


    @Test
    void deleteById_inputValidId_shouldReturnRightMessage() {
        long id = 1;
        ResponseEntity<HolidayDayDTO> responseBeforeDelete = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", HolidayDayDTO.class, id);
        assertEquals(HttpStatus.OK, responseBeforeDelete.getStatusCode());
        assertEquals(holidayDayDTOS.get(0), responseBeforeDelete.getBody());

        testRestTemplate.delete(BASE_URL + "/{id}/delete", id);
        ResponseEntity<String> responseAfterDelete = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", String.class, id);
        assertEquals(HttpStatus.BAD_REQUEST, responseAfterDelete.getStatusCode());
        String expected = "The holidayDay entity doesn't exist with id: 1!";
        assertEquals(expected, responseAfterDelete.getBody());
    }

    @Test
    void save_inputHolidayDayDTO_shouldReturnRightHolidayDayDTO() {
        HolidayDayDTO holidayDayDTO = new HolidayDayDTO();
        holidayDayDTO.setId(3L);
        holidayDayDTO.setYear("2019");
        holidayDayDTO.setLocalDate(Arrays.asList(LocalDate.of(2019, 1, 1), LocalDate.of(2019, 3, 15), LocalDate.of(2019, 4, 2)));

        ResponseEntity<HolidayDayDTO> response = testRestTemplate.postForEntity(BASE_URL + "/add", new HttpEntity<>(holidayDayDTO), HolidayDayDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(holidayDayDTO, response.getBody());
    }

    @Test
    void save_inputHolidayDayDTOWithDuplicatedYear_shouldReturnBadRequest() {
        HolidayDayDTO holidayDayDTO = new HolidayDayDTO();
        holidayDayDTO.setId(3L);
        holidayDayDTO.setYear("2021");
        holidayDayDTO.setLocalDate(Arrays.asList(LocalDate.of(2021, 1, 1), LocalDate.of(2021, 3, 15), LocalDate.of(2021, 4, 2)));

        ResponseEntity<String> response = testRestTemplate.postForEntity(BASE_URL + "/add", new HttpEntity<>(holidayDayDTO), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = "Duplicate entry at holiday days year:2021 is already exists!";
        assertEquals(expected, response.getBody());
    }

    @Test
    void save_inputHolidayDayDTOWithDifferentYearsInTheList_shouldReturnBadRequest() {
        HolidayDayDTO holidayDayDTO = new HolidayDayDTO();
        holidayDayDTO.setId(3L);
        holidayDayDTO.setYear("2020");
        holidayDayDTO.setLocalDate(Arrays.asList(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 3, 15), LocalDate.of(2020, 4, 2)));

        ResponseEntity<String> response = testRestTemplate.postForEntity(BASE_URL + "/add", new HttpEntity<>(holidayDayDTO), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String expected = "The list includes different years! You have to use 2020!";
        assertEquals(expected, response.getBody());
    }


    @Test
    void update_inputHolidayDayDTO_shouldReturnUpdatedHolidayDayDTO() {
        HolidayDayDTO holidayDayDTO = new HolidayDayDTO();
        holidayDayDTO.setId(1L);
        holidayDayDTO.setYear("2021");
        holidayDayDTO.setLocalDate(Arrays.asList(LocalDate.of(2021, 1, 11), LocalDate.of(2021, 3, 15), LocalDate.of(2021, 4, 2)));
        long id = 1;
        ResponseEntity<HolidayDayDTO> responseBeforeUpdate = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", HolidayDayDTO.class, id);
        assertEquals(HttpStatus.OK, responseBeforeUpdate.getStatusCode());
        assertEquals(1, Objects.requireNonNull(responseBeforeUpdate.getBody()).getLocalDate().get(0).getDayOfMonth());

        testRestTemplate.put(BASE_URL + "/{id}/update", new HttpEntity<>(holidayDayDTO), id);
        ResponseEntity<HolidayDayDTO> responseAfterUpdate = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", HolidayDayDTO.class, id);
        assertEquals(HttpStatus.OK, responseAfterUpdate.getStatusCode());
        assertEquals(11, Objects.requireNonNull(responseAfterUpdate.getBody()).getLocalDate().get(0).getDayOfMonth());
    }

    @Test
    void update_inputHolidayDayDTOWithDuplicatedYear_shouldReturnDTOWithoutUpdate() {
        HolidayDayDTO holidayDayDTO = new HolidayDayDTO();
        holidayDayDTO.setId(1L);
        holidayDayDTO.setYear("2022");
        holidayDayDTO.setLocalDate(Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2022, 3, 15), LocalDate.of(2022, 4, 2)));
        long id = 1;
        testRestTemplate.put(BASE_URL + "/{id}/update", new HttpEntity<>(holidayDayDTO), id);
        ResponseEntity<HolidayDayDTO> responseAfterUpdate = testRestTemplate.getForEntity(BASE_URL + "/{id}/get", HolidayDayDTO.class, id);
        assertEquals(HttpStatus.OK, responseAfterUpdate.getStatusCode());
        assertEquals("2021", Objects.requireNonNull(responseAfterUpdate.getBody()).getYear());
    }
}