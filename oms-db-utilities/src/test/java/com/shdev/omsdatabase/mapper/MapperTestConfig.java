package com.shdev.omsdatabase.mapper;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for mapper component scanning in tests.
 */
@Configuration
@ComponentScan(basePackages = "com.shdev.omsdatabase.mapper")
public class MapperTestConfig {
}
