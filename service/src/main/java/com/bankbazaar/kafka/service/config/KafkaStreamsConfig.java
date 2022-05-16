package com.bankbazaar.kafka.service.config;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.service.service.FileStatusService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.KStream;
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
import java.util.function.Consumer;

@Slf4j
@Configuration
public class KafkaStreamsConfig {
    @Autowired
    private FileStatusService fileStatusService;

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
    public Consumer<KStream<String,Data>> fileProcessor(@Qualifier("streamRetryTemplate") RetryTemplate retryTemplate)
    {
        return kStream -> kStream.split()
                .branch(
                        (key, value) ->
                                retryTemplate.execute(
                                    retryContext -> {
                                        try {
                                            fileStatusService.updateEntry(value.getId(),Status.IN_PROGRESS);
                                            ClassLoader classLoader = getClass().getClassLoader();
                                            File file = new File(classLoader.getResource(".").getFile() + value.getFileName());
                                            if (file.exists()) {
                                                fileStatusService.updateEntry(value.getId(),Status.FAILURE);
                                                return false;
                                            }
                                            return true;
                                        }
                                        catch (Exception exception)
                                        {
                                            log.error("retrying",exception);
                                            throw new RuntimeException(exception);
                                        }
                                    },
                                    context -> {
                                        log.error("retries exhausted",context.getLastThrowable());
                                        fileStatusService.updateEntry(value.getId(),Status.ERROR);
                                        return false;
                                    }
                            ),
                        Branched.withConsumer(stream -> stream.to("File_Processor"))
                )
                .branch(
                        (key, value) ->
                                retryTemplate.execute(
                                        retryContext -> {
                                            try {
                                                Status result = fileStatusService.getEntry(value.getId()).getStatus();
                                                return result == Status.FAILURE || result == Status.ERROR;
                                            }
                                            catch (Exception exception)
                                            {
                                                log.error("retrying",exception);
                                                throw new RuntimeException(exception);
                                            }
                                        },
                                        context -> {
                                            log.error("retries exhausted",context.getLastThrowable());
                                            return false;
                                        }

                                ),
                        Branched.withConsumer(stream -> stream.to("Notification"))
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
    public Consumer<KStream<String,Data>> consumer(@Qualifier("streamRetryTemplate") RetryTemplate retryTemplate)
    {
        return kStream -> kStream.split()
                .branch(
                        (key, value) ->
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
                                            }
                                            catch (Exception exception)
                                            {
                                                log.error("retrying",exception);
                                                throw new RuntimeException(exception);
                                            }
                                            return false;
                                        }, context -> {
                                            log.error("retries exhausted",context.getLastThrowable());
                                            fileStatusService.updateEntry(value.getId(),Status.ERROR);
                                            return false;
                                        }
                                )
                )
                .branch(
                        (key, value) ->
                                retryTemplate.execute(
                                        retryContext -> {
                                            try {
                                                Status result = fileStatusService.getEntry(value.getId()).getStatus();
                                                return result == Status.SUCCESS || result == Status.ERROR;
                                            }
                                            catch (Exception exception)
                                            {
                                                log.error("retrying",exception);
                                                throw new RuntimeException(exception);
                                            }
                                        },
                                        context -> {
                                            log.error("retries exhausted",context.getLastThrowable());
                                            return false;
                                        }
                                ),
                        Branched.withConsumer(stream -> stream.to("Notification"))
                );
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
