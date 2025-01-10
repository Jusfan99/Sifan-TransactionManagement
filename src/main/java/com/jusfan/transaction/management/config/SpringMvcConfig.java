package com.jusfan.transaction.management.config;

import java.util.List;

import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.jusfan.transaction.management.interceptor.TransactionInterceptor;

/**
 * @author jiasifan
 * Created on 2025-01-10
 */
@Configuration
class SpringMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 路径匹配配置
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
                // 是否开启后缀模式匹配，如 '/user' 是否匹配 '/user.*'，默认 true
                .setUseSuffixPatternMatch(false)
                // 是否开启后缀路径模式匹配，如 '/user' 是否匹配 '/user/'，默认 true
                .setUseTrailingSlashMatch(true);
    }


    /**
     * 将对于静态资源的请求转发到 Servlet 容器的默认处理静态资源的 Servlet
     * 因为将 Spring 的拦截模式设置为 "/" 时会对静态资源进行拦截
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable("defaultServlet");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(getStringHttpMessageConverter());
        messageConverters.add(getHttpMessageConverter());
        messageConverters.add(getByteArrayHttpMessageConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getTransactionInterceptor());
    }

    @Bean
    public TransactionInterceptor getTransactionInterceptor() {
        return new TransactionInterceptor();
    }


    @Bean
    public MappingJackson2HttpMessageConverter getHttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    public ByteArrayHttpMessageConverter getByteArrayHttpMessageConverter() {
        return new ByteArrayHttpMessageConverter();
    }

    @Bean
    public StringHttpMessageConverter getStringHttpMessageConverter() {
        return new StringHttpMessageConverter();
    }

    @Bean
    public static OrderedCharacterEncodingFilter characterEncodingFilter() {
        OrderedCharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
        filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(false);
        return filter;
    }


}