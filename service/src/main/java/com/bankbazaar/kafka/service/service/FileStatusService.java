package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.manager.FileStatusManager;
import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.dto.model.DataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileStatusService {
    @Autowired
    private FileStatusManager fileStatusManager;

    public FileStatusEntity insert(FileStatusEntity data)
    {
        return fileStatusManager.insert(data);
    }

    public FileStatusEntity update(FileStatusEntity data)
    {
        return fileStatusManager.update(data);
    }

    public FileStatusEntity getEntry(Long id)
    {
        return fileStatusManager.getEntry(id);
    }

    public FileStatusEntity createEntry(DataDto data)
    {
        FileStatusEntity fileData = new FileStatusEntity();
        fileData.setFileName(data.getFileName());
        fileData.setStatus(Status.NEW);
        FileStatusEntity response = insert(fileData);
        return response;
    }
    public void updateEntry(Data data, Status status)
    {
        FileStatusEntity fileData = new FileStatusEntity();
        fileData.setId(data.getId());
        fileData.setStatus(status);
        update(fileData);
    }
}
