package com.bankbazaar.kafka.core.manager;

import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.repository.FileStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class FileStatusManager {

    @Autowired
    private FileStatusRepository fileStatusRepository;


    public FileStatusEntity insert(FileStatusEntity data)
    {
        return fileStatusRepository.save(data);
    }

    public FileStatusEntity update(FileStatusEntity data)
    {
        Optional<FileStatusEntity> presentData = fileStatusRepository.findById(data.getId());
        FileStatusEntity newData = new FileStatusEntity();
        if(presentData.isPresent())
        {
            newData.setId(presentData.get().getId());
            newData.setFileName(presentData.get().getFileName());
            newData.setStatus(data.getStatus());
        }
        return fileStatusRepository.save(newData);
    }
    public Optional<FileStatusEntity> getEntry(Long id)
    {
        return fileStatusRepository.findById(id);
    }
}
