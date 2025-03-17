package com.kefa.infrastructure.aws.config;

import com.kefa.infrastructure.aws.s3.S3Properties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Getter
@Setter
public class AwsProperties {

    private String accessKey;
    private String secretKey;
    private String region;
    private S3Properties s3;

}
