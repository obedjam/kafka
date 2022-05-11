package com.bankbazaar.kafka.service.processor;

import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.dto.model.FileStatusDto;
import com.bankbazaar.kafka.service.service.FileStatusService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.function.Function;
@Slf4j
@Service
public class FileValidationProcessor {

    @Autowired
    private FileStatusService fileStatusService;

    @Bean
    public Function<KStream<String,Data>, KStream<String,Data>> fileProcessor()
    {
        return kStream -> kStream.filter((key, value) -> {
            FileStatusDto fileData = new FileStatusDto();
            fileData.setId(value.getId());
            fileData.setStatus("IN_PROGRESS");
            fileStatusService.update(fileData);

            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource(".").getFile() + value.getFileName());
            if(file.exists())
            {
                fileData.setStatus("FAILURE");
                fileStatusService.update(fileData);
                return false;
            }

            return true;
        });

    }
}
