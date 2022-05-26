package com.bankbazaar.kafka.service.service;

import com.bankbazaar.kafka.core.manager.FileStatusManager;
import com.bankbazaar.kafka.core.model.FileStatusEntity;
import com.bankbazaar.kafka.core.model.Status;
import com.bankbazaar.kafka.core.repository.FileStatusRepository;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@AutoConfigureMockMvc
public class RedisCacheTest {

    @Autowired
    protected MockMvc mvc;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private FileStatusRepository fileStatusRepository;
    @Autowired
    private FileStatusManager fileStatusManager;

    @Test
    void redisCacheTest() throws Exception {

        ClassLoader classLoader = getClass().getClassLoader();

        /**
         * controller returns null before any db entry
         */
        assertNull(fileStatusManager.getEntry("test1.csv"));

        /**
         * Create file object dataDto1
         * Insert to DB
         */
        FileStatusEntity response = createEntryObject("test1.csv");
        assertEquals(fileStatusManager.getEntry("test1.csv"),response.getStatus());

        response = updateEntryObject(response, Status.IN_PROGRESS);
        assertEquals(fileStatusManager.getEntry("test1.csv"),response.getStatus());

        response = updateEntryObject(response, Status.SUCCESS);
        assertEquals(fileStatusManager.getEntry("test1.csv"),response.getStatus());

        /**
         *
         * Verify Cache StatusCache is deployed
         */
        assertNotNull(cacheManager.getCache("StatusCache"));

        /**
         * Clear DB
         * Verify status from cache
         */
        fileStatusRepository.deleteAll();
        assertEquals(fileStatusManager.getEntry("test1.csv"),Status.SUCCESS);

        /**
         * Verify controller returns null after cache entry times out
         */
        await().atMost(Durations.TEN_SECONDS).until(()-> fileStatusManager.getEntry("test1.csv") == null);
    }
    private FileStatusEntity createEntryObject(String fileName)
    {
        FileStatusEntity data = new FileStatusEntity();
        data.setFileName(fileName);
        data.setStatus(Status.NEW);
        return fileStatusManager.insert(data);
    }
    private FileStatusEntity updateEntryObject(FileStatusEntity data, Status status)
    {
        data.setStatus(status);
        return fileStatusManager.update(data);
    }
}
