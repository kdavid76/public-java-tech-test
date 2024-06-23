package com.global.aod.interview.techtest.configuration;

import com.global.aod.interview.techtest.mapper.StringToFieldsMapper;
import com.global.aod.interview.techtest.mapper.StringToSortDirectionMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToFieldsMapper());
        registry.addConverter(new StringToSortDirectionMapper());
    }
}
