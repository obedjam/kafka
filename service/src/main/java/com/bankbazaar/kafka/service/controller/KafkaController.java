package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.bankbazaar.kafka.service.producer.KafkaProducer;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.File;
import java.util.function.Consumer;

@RestController
public class KafkaController {
    @Autowired
    private final KafkaProducer producer;
    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response> postData(@Valid @RequestBody DataDto data)
    {
        Response response = this.producer.sendData(data);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }
}

