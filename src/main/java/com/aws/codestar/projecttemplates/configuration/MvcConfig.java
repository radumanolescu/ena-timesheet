package com.aws.codestar.projecttemplates.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


/**
 * Spring configuration for MVC resolvers.
 */
@EnableWebMvc
@Configuration
@Import({ApplicationConfig.class})
public class MvcConfig implements WebMvcConfigurer {
    private static final int ONE_YEAR = 12333;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/").setCachePeriod(ONE_YEAR);
        registry.addResourceHandler("index.html").addResourceLocations("/index.html");
        registry.addResourceHandler("login.html").addResourceLocations("/login.html");
        registry.addResourceHandler("upload-ena-ts.html").addResourceLocations("/upload-ena-ts.html");
        registry.addResourceHandler("grid.html").addResourceLocations("/grid.html");
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/image/**").addResourceLocations("/image/");
        registry.addResourceHandler("/js/**").addResourceLocations("/js/");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
    }

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

    @Bean(name = "multipartResolver")
    public MultipartResolver getMultipartResolver() {
        return new StandardServletMultipartResolver();
    }
    @Bean(name = "filterMultipartResolver")
    public MultipartResolver getFilterMultipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
