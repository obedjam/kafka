package com.bankbazaar.kafka.service.producer;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.dto.model.FileStatusDto;
import com.bankbazaar.kafka.service.mapper.DataMapper;
import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.bankbazaar.kafka.service.service.FileStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;

@Slf4j
@Service
public class KafkaProducer {
    private  static  final  String TOPIC = "Create_CSV";

    @Autowired
    private  KafkaTemplate<String, Data> kafkaTemplate;
    @Autowired
    private FileStatusService fileStatusService;

    @Autowired
    private DataMapper mapper;

    public Response sendData(DataDto data)
    {
        FileStatusDto fileData = new FileStatusDto();
        fileData.setFileName(data.getFileName());
        fileData.setStatus("NEW");
        FileStatusDto response = fileStatusService.insert(fileData);
        data.setId(response.getId());
        this.kafkaTemplate.send(TOPIC,mapper.dtoToDomain(data));

        FileStatusDto controllerResponse = fileStatusService.getEntry(response.getId());
        return new Response(controllerResponse.getId(), controllerResponse.getStatus(), new Date());


    }
}
