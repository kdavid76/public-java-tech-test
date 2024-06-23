package com.global.aod.interview.techtest.mapper;

import com.global.aod.interview.techtest.model.Fields;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

public class StringToFieldsMapper implements Converter<String, Fields> {

    @Override
    public Fields convert(String source) {
        var field = Fields.ID;
        if (StringUtils.isNotBlank(source)) {
            try {
                field = Fields.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return field;
    }
}
