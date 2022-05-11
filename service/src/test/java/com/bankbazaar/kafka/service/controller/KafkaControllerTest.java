package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.core.repository.FileStatusRepository;
import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileReader;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
class KafkaControllerTest {

    @Autowired
    protected MockMvc mvc;
    @InjectMocks
    private KafkaController kafkaController;
    @Autowired
    private FileStatusRepository fileStatusRepository;

    @Test
    void userController() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        DataDto dataDto1 = createFileObject("test1.csv");
        File file1 = new File(classLoader.getResource(".").getFile() + dataDto1.getFileName());
        assertFalse(file1.exists());
        Response responseData1 = consumeApi(dataDto1);

        DataDto dataDto2 = createFileObject("test2.csv");
        File file2 = new File(classLoader.getResource(".").getFile() + dataDto2.getFileName());
        assertFalse(file2.exists());
        Response responseData2 = consumeApi(dataDto2);

        await().atMost(Durations.TEN_SECONDS).until(file1::exists);
        validateTrue(responseData1, dataDto1, file1);

        await().atMost(Durations.TEN_SECONDS).until(file2::exists);
        validateTrue(responseData2, dataDto2, file2);

        DataDto dataDto3 = createFileObject("test2.csv");
        File file3 = new File(classLoader.getResource(".").getFile() + dataDto3.getFileName());
        Response responseData3 = consumeApi(dataDto3);

        int index = fileStatusRepository.getById(responseData3.getExecutionId()).getStatus().ordinal();
        while (index<=Status.IN_PROGRESS.ordinal())
        {
            index = fileStatusRepository.getById(responseData3.getExecutionId()).getStatus().ordinal();
        }
        assertTrue(file3.exists());
        validateFalse(responseData3, dataDto3, file3);

        file1.delete();
        file2.delete();
    }

    private DataDto createFileObject(String fileName)
    {
        String [] data = {"val1|val2|val3","val4|val5|val6"};
        DataDto dataDto = new DataDto();
        dataDto.setFileName(fileName);
        dataDto.setHeaders("col1,col2,col3");
        dataDto.setData(data);
        return dataDto;
    }

    private Response consumeApi(DataDto dataDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult response = mvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dataDto)))
                .andExpect(status().is(202)).andReturn();

        Response data = objectMapper.readValue(response.getResponse().getContentAsString(), Response.class);
        return data;
    }

    private void validateTrue(Response responseData, DataDto dataDto, File fileCsv) throws Exception {
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
        assertEquals(i,dataDto.getData().length);

        assertEquals(responseData.getStatus(),"NEW");;
        FileStatusEntity data = fileStatusRepository.getById(responseData.getExecutionId());
        assertEquals(data.getStatus(), Status.SUCCESS);
    }

    private void validateFalse(Response responseData, DataDto dataDto, File fileCsv) throws Exception {
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
        assertEquals(i,dataDto.getData().length);

        assertEquals(responseData.getStatus(),"NEW");;
        FileStatusEntity data = fileStatusRepository.getById(responseData.getExecutionId());
        assertEquals(data.getStatus(), Status.FAILURE);
    }
}