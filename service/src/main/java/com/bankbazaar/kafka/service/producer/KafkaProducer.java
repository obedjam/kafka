package com.bankbazaar.kafka.service.producer;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.service.mapper.DataMapper;
import com.bankbazaar.kafka.dto.model.DataDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private  static  final  String TOPIC = "Create_CSV";

    @Autowired
    private  KafkaTemplate<String, Data> kafkaTemplate;

    @Autowired
    private DataMapper mapper;

    public void sendData(DataDto data)
    {
        this.kafkaTemplate.send(TOPIC,mapper.dtoToDomain(data));
    }
}
