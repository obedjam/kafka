package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class KafkaController {
    @Autowired
    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @RequestMapping(method = RequestMethod.POST)
    public void postData(@Valid @RequestBody DataDto data)
    {
        this.producer.sendData(data);
    }
}
