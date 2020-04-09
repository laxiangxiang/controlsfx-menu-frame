package com.shdq.menu_frame.frame.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shdq-fjy
 */
public class RestTemplateUtil {
    private static RestTemplate restTemplate;
    private String s;

    public static RestTemplate getRestTemplate(){
        SingleHold.restTemplate.getMessageConverters().add(new HttpMessageConverter());
        return SingleHold.restTemplate;
    }

    static class SingleHold{
        private static RestTemplate restTemplate = new RestTemplate();
    }

    public RestTemplate obtainRestTemplate(){
        if (StringUtils.isBlank(s)){
            return restTemplate();
        }
        if (s.equals("urlConnection")){
            return urlConnectionRestTemplate();
        }
        if (s.equals("httpClient")){
            return httpClientRestTemplate();
        }
        if (s.equals("OKHttp3")){
            return OKHttp3RestTemplate();
        }
        return restTemplate();
    }

    public RestTemplateUtil(String s) {
        this.s = s;
    }

    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public RestTemplate urlConnectionRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new HttpMessageConverter());
        return restTemplate;
    }

    public RestTemplate httpClientRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

    public RestTemplate OKHttp3RestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        return restTemplate;
    }

    static class HttpMessageConverter extends MappingJackson2HttpMessageConverter {
        public HttpMessageConverter() {
            List<MediaType> mediaTypes = new ArrayList<>();
            mediaTypes.add(MediaType.TEXT_HTML);
            mediaTypes.add(MediaType.TEXT_PLAIN);
            mediaTypes.add(MediaType.APPLICATION_JSON);
            setSupportedMediaTypes(mediaTypes);
        }
    }
}
