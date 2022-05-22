package com.bankbazaar.kafka.service.controller;

import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.dto.model.DataDto;
import com.bankbazaar.kafka.service.model.Response;
import com.bankbazaar.kafka.service.producer.KafkaProducer;
import com.bankbazaar.kafka.service.service.FileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class KafkaController {
    @Autowired
    private final KafkaProducer producer;

    @Autowired
    private FileStatusService fileStatusService;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Response> postData(@Valid @RequestBody DataDto data)
    {
        Response response = this.producer.sendData(data);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Status> getStatusFromCache(@Valid @RequestParam Long id)
    {
        Status response = fileStatusService.getFromCache(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}

