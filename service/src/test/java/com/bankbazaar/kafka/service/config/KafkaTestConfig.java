package com.bankbazaar.kafka.service.config;


import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.service.service.FileStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfiguration
public class KafkaTestConfig{

    @Autowired
    private FileStatusService fileStatusService;

    /**
     *Validate status of each notification
     */
    @KafkaListener(topics = "Notification", groupId="Kafka_CSV_Creation")
    public void notificationValidation(Data value) {
        if(value.getId()==1L)
        {
            assertEquals(fileStatusService.getEntry(value.getId()).getStatus(), Status.SUCCESS);
        }
        else if (value.getId()==2L)
        {
            assertEquals(fileStatusService.getEntry(value.getId()).getStatus(), Status.FAILURE);
        }
        else if (value.getId()==3L)
        {
            assertEquals(fileStatusService.getEntry(value.getId()).getStatus(), Status.SUCCESS);
        }
        else if (value.getId()==4L)
        {
            assertEquals(fileStatusService.getEntry(value.getId()).getStatus(), Status.FAILURE);
        }
    }
}
