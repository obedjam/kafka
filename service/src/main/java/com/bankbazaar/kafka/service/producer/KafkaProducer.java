package com.bankbazaar.kafka.service.producer;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.service.mapper.DataMapper;
import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.bankbazaar.kafka.service.service.FileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

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
        FileStatusEntity response = fileStatusService.createEntry(data);
        data.setId(response.getId());
        this.kafkaTemplate.send(TOPIC,mapper.dtoToDomain(data));
        return new Response(response.getId(), response.getStatus().toString(), new Date());
    }
}
