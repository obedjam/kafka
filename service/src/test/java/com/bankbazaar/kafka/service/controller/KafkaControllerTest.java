package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.core.repository.FileStatusRepository;
import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileReader;
import java.util.function.BooleanSupplier;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
public class KafkaControllerTest{

    @Autowired
    protected MockMvc mvc;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private FileStatusRepository fileStatusRepository;

    @Test
    void userController() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        assertEquals(consumeApi("test2.csv"), Status.NULL);

        /**
         * Create file object dataDto1
         * Assert file test1.csv doesn't exist
         * Feed dataDto1 to controller
         */
        DataDto dataDto1 = createFileObject("test1.csv");
        File file1 = new File(classLoader.getResource(".").getFile() + dataDto1.getFileName());
        assertFalse(file1.exists());
        Response responseData1 = consumeApi(dataDto1);

        /**
         * Assert file test1.csv is created
         * Validate contents
         */
        await().atMost(Durations.TEN_SECONDS).until(file1::exists);
        validateTrue(dataDto1, file1);
        assertEquals(consumeApi(responseData1.getExecutionId()), Status.SUCCESS);

        /**
         * Create file object dataDto3
         * Assert file test2.csv already exist
         * Feed dataDto3 to controller
         */
        DataDto dataDto3 = createFileObject("test1.csv");
        File file3 = new File(classLoader.getResource(".").getFile() + dataDto3.getFileName());
        assertTrue(file3.exists());
        consumeApi(dataDto3);

        /**
         * Create file object dataDto2
         * Assert file test2.csv doesn't exist
         * Feed dataDto2 to controller
         */
        DataDto dataDto2 = createFileObject("test2.csv");
        File file2 = new File(classLoader.getResource(".").getFile() + dataDto2.getFileName());
        assertFalse(file2.exists());
        Response responseData2 = consumeApi(dataDto2);

        /**
         * Assert file test2.csv is created
         * Validate contents
         */
        await().atMost(Durations.TEN_SECONDS).until(file2::exists);
        validateTrue(dataDto2, file2);
        assertEquals(consumeApi(responseData2.getExecutionId()), Status.SUCCESS);
        await().atMost(Durations.TEN_SECONDS).until(()->consumeApi("test2.csv").equals(Status.SUCCESS));
        assertNotNull(cacheManager.getCache("StatusCache"));

        /**
         * Create dataDto4 with empty fields
         * Validate 400 bad request response
         */
        DataDto dataDto4 = createBadFileObject();
        consumeBadApi(dataDto4);

        Thread.sleep(5000);
        assertEquals(consumeApi(responseData1.getExecutionId()), Status.SUCCESS);
        assertEquals(consumeApi(responseData2.getExecutionId()), Status.SUCCESS);

        assertEquals(consumeApi("test2.csv"), Status.SUCCESS);
        fileStatusRepository.deleteAll();
        assertEquals(consumeApi("test2.csv"), Status.SUCCESS);

        await().atMost(Durations.TEN_SECONDS).until(()->consumeApi("test2.csv").equals(Status.NULL));

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
    private DataDto createBadFileObject()
    {
        String [] data= new String[0];
        DataDto dataDto = new DataDto();
        dataDto.setFileName("");
        dataDto.setHeaders("");
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

    private Status consumeApi(Long id) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult response = mvc.perform(get("/?id="+id.toString()))
                .andExpect(status().is(200)).andReturn();

        Status data = objectMapper.readValue(response.getResponse().getContentAsString(), Status.class);
        return data;
    }

    private Status consumeApi(String name) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult response = mvc.perform(get("/name?name="+name))
                .andExpect(status().is(200)).andReturn();

        Status data = objectMapper.readValue(response.getResponse().getContentAsString(), Status.class);
        return data;
    }
    private void consumeBadApi(DataDto dataDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        mvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dataDto)))
                .andExpect(status().is(400));
    }

    private void validateTrue(DataDto dataDto, File fileCsv) throws Exception {
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

    }

    public boolean checkStatus(Response response, Status status) throws Exception {
        return consumeApi(response.getExecutionId()).equals(Status.SUCCESS);
    }
}