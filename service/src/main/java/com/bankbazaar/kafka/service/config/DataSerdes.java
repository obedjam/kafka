package com.bankbazaar.kafka.service.config;

import com.bankbazaar.kafka.core.model.Data;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;

public final class DataSerdes{
    public static Serde<Data> DataSerde() {
        JsonSerializer<Data> serializer = new JsonSerializer<>();
        JsonDeserializer<Data> deserializer = new JsonDeserializer<>(Data.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}