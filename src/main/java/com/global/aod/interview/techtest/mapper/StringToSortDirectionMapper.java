package com.global.aod.interview.techtest.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Sort;

public class StringToSortDirectionMapper implements Converter<String, Sort.Direction> {

    @Override
    public Sort.Direction convert(String source) {
        var sortDirection = Sort.Direction.ASC;
        if (StringUtils.isNotBlank(source)) {
            try {
                sortDirection = Sort.Direction.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return sortDirection;
    }
}
