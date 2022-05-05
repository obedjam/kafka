package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.dto.model.DataDto;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class KafkaControllerTest {

    @Autowired
    private KafkaController kafkaController;

    @Test
    void postData() throws IOException, CsvValidationException {
        String [] data = {"val1|val2|val3","val4|val5|val6"};
        File fileCsv = new File("test.csv");
        assertFalse(fileCsv.exists());
        DataDto dataDto = new DataDto();
        dataDto.setFileName("test.csv");
        dataDto.setHeaders("col1,col2,col3");
        dataDto.setData(data);
        kafkaController.postData(dataDto);
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


    }
}