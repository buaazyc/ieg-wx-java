package com.tencent.wxcloudrun.domain.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.util.Arrays;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author zhangyichuan
 */
@Configuration
public class AppConfig {
  @Bean
  public RestTemplate restTemplate() {
    // 创建支持自动解压 GZIP 的 HttpClient 实例
    CloseableHttpClient httpClient = HttpClients.custom().setUserAgent("your-user-agent").build();

    // 创建请求工厂并启用自动解压 GZIP 响应
    HttpComponentsClientHttpRequestFactory factory =
            new HttpComponentsClientHttpRequestFactory(httpClient);

    // 创建自定义的 ObjectMapper 来过滤非法字符
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 创建XML映射器
    XmlMapper xmlMapper = new XmlMapper();
    xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    RestTemplate restTemplate = new RestTemplate(factory);

    // 添加JSON转换器
    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter(mapper);

    // 添加XML转换器
    MappingJackson2XmlHttpMessageConverter xmlConverter = new MappingJackson2XmlHttpMessageConverter(xmlMapper);
    xmlConverter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_XML,
            MediaType.TEXT_XML,
            new MediaType("text", "xml", java.nio.charset.StandardCharsets.UTF_8)
    ));

    // 设置消息转换器
    restTemplate.getMessageConverters().set(0, jsonConverter);
    restTemplate.getMessageConverters().add(1, xmlConverter);

    return restTemplate;
  }
}