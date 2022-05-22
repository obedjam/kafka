package com.bankbazaar.kafka.service.config;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.service.service.FileStatusService;
import com.bankbazaar.kafka.service.service.StatusCacheService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.StreamRetryTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@Configuration
public class KafkaStreamsConfig implements Serializable {
    @Autowired
    private FileStatusService fileStatusService;
    @Autowired
    private StatusCacheService statusCacheService;

    @Value("${spring.datasource.maxRetries}")
    private Integer maxRetries;

    /**
     *Setup retry template bean.
     */
    @Bean
    @StreamRetryTemplate
    RetryTemplate streamRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        RetryPolicy retryPolicy = new SimpleRetryPolicy(maxRetries);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(1);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }
    /**
     * Set status to IN_PROGRESS.
     * Consumes data from Create_CSV topic.
     * Validates if file already exits.
     * Set status to FAILURE if file already exists.
     * Output data to File_Processor topic.
     */

    @Bean
    public Consumer<KStream<String, Data>>fileProcessor(@Qualifier("streamRetryTemplate") RetryTemplate retryTemplate)
    {
        return kStream -> kStream.map((key, value) ->
                                retryTemplate.execute(
                                        retryContext -> {
                                                fileStatusService.updateEntry(value.getId(),Status.IN_PROGRESS);
                                                ClassLoader classLoader = getClass().getClassLoader();
                                                File file = new File(classLoader.getResource(".").getFile() + value.getFileName());
                                                if (file.exists()) {
                                                    fileStatusService.updateEntry(value.getId(),Status.FAILURE);
                                                    statusCacheService.saveStatus(value.getId(),Status.FAILURE);
                                                }
                                            return new KeyValue<>(key,value);
                                        },
                                        context -> {
                                            log.error("retries exhausted",context.getLastThrowable());
                                            fileStatusService.updateEntry(value.getId(),Status.ERROR);
                                            statusCacheService.saveStatus(value.getId(),Status.ERROR);
                                            return new KeyValue<>(key,value);
                                        }
                                )
                ).filter((key, value) -> value != null).split()
                .branch(
                        (key, value) -> fileStatusService.getEntry(value.getId()).getStatus().ordinal()<=2,
                        Branched.withConsumer(stream -> stream.to("File_Processor",Produced.with(Serdes.String(), DataSerdes.DataSerde())))
                )
                .branch(
                        (key, value) -> fileStatusService.getEntry(value.getId()).getStatus().ordinal()>2,
                        Branched.withConsumer(stream -> stream.to("Notification",Produced.with(Serdes.String(), DataSerdes.DataSerde())))
                );
    }

    /**
     * Consumes data from File_Processor topic.
     * Create.csv file and populate file using data.
     * Set status to SUCCESS if file IS created successfully.
     * Set status to ERROR if exception occurs during file creation.
     * Retry a maximum of 1 time if exception occurs;
     */
    @Bean
    public Function<KStream<String,Data>, KStream<String,Data>> consumer(@Qualifier("streamRetryTemplate") RetryTemplate retryTemplate)
    {
        return kStream -> kStream.map((key, value) ->
                        retryTemplate.execute(
                                retryContext -> {
                                    try{
                                        ClassLoader classLoader = getClass().getClassLoader();
                                        File file = new File(classLoader.getResource(".").getFile() + value.getFileName());
                                        FileWriter fileWriter = new FileWriter(file,false);
                                        CSVWriter CsvWriter = new CSVWriter(fileWriter);
                                        CsvWriter.writeNext(value.getHeaders());
                                        for (String[] element : value.getData()) {
                                            CsvWriter.writeNext(element);
                                        }
                                        CsvWriter.close();
                                        fileWriter.close();
                                        fileStatusService.updateEntry(value.getId(),Status.SUCCESS);
                                        statusCacheService.saveStatus(value.getId(),Status.SUCCESS);
                                        return new KeyValue<>(key,value);
                                    }
                                    catch (Exception exception)
                                    {
                                        log.error("retrying",exception);
                                        throw new RuntimeException(exception);
                                    }
                                }, context -> {
                                    log.error("retries exhausted",context.getLastThrowable());
                                    fileStatusService.updateEntry(value.getId(),Status.ERROR);
                                    statusCacheService.saveStatus(value.getId(),Status.ERROR);
                                    return new KeyValue<>(key,value);
                                }
                                )
        ).filter((key, value) -> value != null);

    }

    @Bean
    public Consumer<KStream<String,Data>> notification()
    {
        return kStream -> kStream.foreach((key, value) ->
        {
            log.info(fileStatusService.getEntry(value.getId()).getStatus().toString());
        });
    }
}
