package com.bankbazaar.kafka.service.consumer;

import com.bankbazaar.kafka.core.model.Data;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

@Slf4j
@Service
public class KafkaConsumer {
    @KafkaListener(topics = "create_CSV")
    public void getData(Data data) throws IOException {

        try {
            Writer fileWriter = new FileWriter(data.getFileName(), false);
            CSVWriter CsvWriter = new CSVWriter(fileWriter);
            CsvWriter.writeNext(data.getHeaders());

            for(String[] element : data.getData())
            {
                CsvWriter.writeNext(element);
            }
            CsvWriter.close();
        }
        catch (Exception e){log.error("Failed to write to csv");}
    }
}
