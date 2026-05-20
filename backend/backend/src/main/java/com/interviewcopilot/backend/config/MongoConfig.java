package com.interviewcopilot.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // WHY: Enables @CreatedDate and @LastModifiedDate to auto-populate
public class MongoConfig {
    // MongoDB connection is handled by application-dev.properties
}