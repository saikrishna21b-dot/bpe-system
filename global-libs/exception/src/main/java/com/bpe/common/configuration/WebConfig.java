package com.bpe.common.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bpe.common.exception.TraceIdFilter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new TraceIdFilter());
        reg.addUrlPatterns("/*");
        reg.setOrder(1);
        return reg;
    }
}
