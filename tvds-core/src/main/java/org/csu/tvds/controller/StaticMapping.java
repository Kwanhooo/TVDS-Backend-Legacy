package org.csu.tvds.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration(proxyBeanMethods = false)
public class StaticMapping extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("静态资源映射");
        registry.addResourceHandler("/cache/**")
                .addResourceLocations("classpath:/static/");
        // 获得classpath的绝对路径
        String path = StaticMapping.class.getResource("/static").getPath();
        System.out.println(path);
    }
}