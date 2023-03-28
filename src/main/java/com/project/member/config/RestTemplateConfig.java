package com.project.member.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * packageName   : com.project.member.config
 * fileName      : RestTemplateConfig
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(){
        //Apache HttpComponents HttpClient
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(50) // 최대 커넥션 수
                .setMaxConnPerRoute(10).build(); // 각 호스트 당 커넥션 풀에 생성가능한 커넥션 수

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(5000); // read timeout
        factory.setConnectTimeout(3000); // connection timeout
        factory.setHttpClient(httpClient);

        // 기본생성자로 초기화시 SimpleClientHttpRequestFactory 사용
        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));

        return restTemplate;
    }
}
