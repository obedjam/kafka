package com.bankbazaar.kafka.service.consumer;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.dto.model.FileStatusDto;
import com.bankbazaar.kafka.service.service.FileStatusService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.function.Consumer;

@Slf4j
@Service
public class KafkaConsumer {
    @Autowired
    private FileStatusService fileStatusService;


    @Bean
    public Consumer<KStream<String,Data>> consumer(@Qualifier("streamRetryTemplate") RetryTemplate retryTemplate)
    {
        return kStream -> kStream.foreach((key, value) ->
                {
                    try {
                        retryTemplate.execute(
                                retryContext -> {
                                    try {
                                        FileStatusDto fileData = new FileStatusDto();
                                        fileData.setId(value.getId());
                                        Writer fileWriter = new FileWriter(value.getFileName(), false);
                                        CSVWriter CsvWriter = new CSVWriter(fileWriter);
                                        CsvWriter.writeNext(value.getHeaders());
                                        for (String[] element : value.getData()) {
                                            CsvWriter.writeNext(element);
                                        }
                                        CsvWriter.close();
                                        fileData.setStatus("SUCCESS");
                                        fileStatusService.update(fileData);
                                    }
                                    catch (Exception exception)
                                    {
                                        log.error("retrying",exception);
                                        throw exception;
                                    }
                                    return null;
                                }, context -> {
                                    log.error("retries exhausted");
                                    FileStatusDto fileData = new FileStatusDto();
                                    fileData.setId(value.getId());
                                    fileData.setStatus("ERROR");
                                    fileStatusService.update(fileData);
                                    return null;
                                }
                                );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

    }
}
