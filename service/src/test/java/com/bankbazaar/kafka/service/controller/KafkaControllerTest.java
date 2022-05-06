package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.dto.model.DataDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class KafkaControllerTest {

    @Autowired
    protected MockMvc mvc;
    @InjectMocks
    private KafkaController kafkaController;
    @Test
    void userController() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        String [] data = {"val1|val2|val3","val4|val5|val6"};
        File fileCsv = new File("test.csv");
        assertFalse(fileCsv.exists());
        DataDto dataDto = new DataDto();
        dataDto.setFileName("test.csv");
        dataDto.setHeaders("col1,col2,col3");
        dataDto.setData(data);

        mvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dataDto)))
                .andExpect(status().is(200));

        await().atMost(Duration.TEN_SECONDS).until(fileCsv::exists);

        FileReader filereader = new FileReader(fileCsv);
        CSVReader csvReader = new CSVReader(filereader);
        String[] nextRecord;
        nextRecord = csvReader.readNext();

        assertArrayEquals(nextRecord, dataDto.getHeaders().split(","));

        int i=0;
        while ((nextRecord = csvReader.readNext()) != null)
        {
            assertArrayEquals(nextRecord, dataDto.getData()[i].split("\\|"));
            i++;
        }
        assertEquals(i,data.length);
    }
}