package com.bankbazaar.kafka.service.config;


import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.model.Status;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfiguration
public class KafkaTestConfig {
    /**
     *Validate status of each notification
     */
    @KafkaListener(topics = "Notification", groupId="Kafka_CSV_Creation")
    public void notificationValidation(final FileStatusEntity value) {
        if(value.getId()==1L)
        {
            assertEquals(value.getStatus(), Status.SUCCESS);
        }
        else if (value.getId()==2L)
        {
            assertEquals(value.getStatus(), Status.SUCCESS);
        }
        else if (value.getId()==3L)
        {
            assertEquals(value.getStatus(), Status.FAILURE);
        }
        else if (value.getId()==4L)
        {
            assertEquals(value.getStatus(), Status.FAILURE);
        }
    }
}
