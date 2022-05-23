package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.manager.FileStatusManager;
import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.dto.model.DataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileStatusService {
    @Autowired
    private FileStatusManager fileStatusManager;

    @Autowired
    private RedisUtil redisUtil;

    public FileStatusEntity insert(FileStatusEntity data)
    {
        return fileStatusManager.insert(data);
    }

    public FileStatusEntity update(FileStatusEntity data)
    {
        return fileStatusManager.update(data);
    }

    public FileStatusEntity createEntry(DataDto data)
    {
        FileStatusEntity fileData = new FileStatusEntity();
        fileData.setFileName(data.getFileName());
        fileData.setStatus(Status.NEW);
        FileStatusEntity response = insert(fileData);
        return response;
    }
    public FileStatusEntity getEntry(Long id)
    {
        return fileStatusManager.getEntry(id).get();
    }
    public FileStatusEntity updateEntry(Long id, Status status)
    {
        FileStatusEntity fileData = new FileStatusEntity();
        fileData.setId(id);
        fileData.setStatus(status);
        FileStatusEntity response = update(fileData);
        return response;
    }
    public Status getFileStatus(Long id)
    {
        Status status = (Status) redisUtil.getFromRedis(id);
        if(status!=null)
        {
            return status;
        }
        status = getEntry(id).getStatus();
        redisUtil.saveToRedis(id,status);
        return status;
    }

}
