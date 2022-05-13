package com.bankbazaar.kafka.service.config;

import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.producer.KafkaProducer;
import com.bankbazaar.kafka.service.service.FileStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.bankbazaar.kafka.service.controller.KafkaControllerTest;

public class KafkaStreamsTest extends KafkaControllerTest{

    @Autowired
    KafkaProducer kafkaProducer;
    @Autowired
    private FileStatusService fileStatusService;

    @Test
    void sendData() throws InterruptedException {
        DataDto dataDto = createFileObject("");
        kafkaProducer.sendData(dataDto);
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


}