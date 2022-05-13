package com.bankbazaar.kafka.service.mapper;
import com.bankbazaar.kafka.core.model.Data;
import com.bankbazaar.kafka.dto.model.DataDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DataMapper {

    @Mapping(source = "data", target = "data", qualifiedByName = "mapData")
    @Mapping(source = "headers", target = "headers", qualifiedByName = "mapColumn")
    Data dtoToDomain(DataDto dto);

    @Named("mapData")
    public static List<String[]> mapData(String[] data) {
        List<String[]> newData = new ArrayList<>();
        for(String value : data)
        {
            newData.add(value.split("\\|"));
        }
        return newData;
    }

    @Named("mapColumn")
    public static String[] mapColumn(String headers) {
        return headers.split(",");
    }
}
