package com.shdq.menu_frame.frame.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shdq.menu_frame.http.Result;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
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
            mediaTypes.add(MediaType.MULTIPART_FORM_DATA);
            setSupportedMediaTypes(mediaTypes);
        }
    }

    private static final String filePath = "C:/project/mine/controlsfx-menu-frame/src/main/resources/images/";
    /**
     * 文件上传
     * @return
     */
    public static Result fileUpload(File file,String username){
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        headers.setContentType(type);
        //设置请求体
        FileSystemResource fileSystemResource = new FileSystemResource(file.getAbsolutePath());
        MultiValueMap<String,Object> form = new LinkedMultiValueMap<>();
        form.add("file",fileSystemResource);//后台接口参数名称
        form.add("username",username);
        //用httpEntity封装整个请求报文
        HttpEntity<MultiValueMap<String,Object>> files = new HttpEntity<>(form,headers);
        Result result = RestTemplateUtil.getRestTemplate().postForObject("http://localhost:8080/user/uploadUserHeadImage",files,Result.class);
        return result;
    }

    /**
     * 文件下载
     * @param fileName
     * @return
     */
    public static File filedDownload(String fileName){
        RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
        File dest = new File(filePath+File.separator+fileName+".png");
        File file = restTemplate.execute("http://localhost:8080/user/downloadUserHeadImage?fileName="+fileName, HttpMethod.GET, null, clientHttpResponse -> {
//            File ret = File.createTempFile("download", "tmp");
            System.out.println(clientHttpResponse.getBody());
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(dest));
            return dest;
        });
        return file;
    }

//    public File fileDownload2(String url){
//        RestTemplate restTemplate = RestTemplateUtil.getRestTemplate();
//        //检查服务端是否支持恢复下载
//        HttpHeaders headers = restTemplate.headForHeaders(url);
//        if (headers.get("Accept-Ranges").contains("bytes") && headers.getContentLength() > 0){
//            restTemplate.execute(
//                    url,
//                    HttpMethod.GET,
//                    clientHttpRequest -> clientHttpRequest.getHeaders().set(
//                            "Range",
//                            String.format("bytes=%d-%d", file.length(), headers.getContentLength())),
//                    clientHttpResponse -> {
//                        File ret = File.createTempFile("download", "tmp");
//                        StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret, true));
//                        return ret;
//                    });
//        }
//    }
}
