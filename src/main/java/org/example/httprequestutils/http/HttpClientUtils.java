package com.shj.steam_gui.utils.http;

import com.shj.steam_gui.utils.http.entity.RequestEntity;
import com.shj.steam_gui.utils.http.entity.RequestType;
import com.shj.steam_gui.utils.http.entity.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClientUtils {

    private static final String STRING_RES_TYPE = "STRING";
    private static final String BYTE_RES_TYPE = "BYTE";

    //执行请求
    public static ResponseEntity doRequest(RequestEntity requestEntity) {
        if (requestEntity.getHttpClient() == null) {
            requestEntity.buildHttpClient();
        }
        switch (requestEntity.getRequestType()) {
            case RequestType.GET_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doGetRequest(requestEntity, STRING_RES_TYPE, true));
            case RequestType.PROTO_GET_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doGetRequest(requestEntity, BYTE_RES_TYPE, true));
            case RequestType.POST_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doPostRequest(requestEntity, STRING_RES_TYPE, true));
            case RequestType.PROTO_POST_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doPostRequest(requestEntity, BYTE_RES_TYPE, true));
            case RequestType.DOWNLOAD_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doDownloadRequest(requestEntity, true));
            case RequestType.PUT_TYPE:
                return requestEntity.getHttpResult().doResult(requestEntity, doPutRequest(requestEntity, STRING_RES_TYPE, true));
        }
        return new ResponseEntity("-1", "不支持的请求方式", "不支持的请求方式");
    }

    //下载请求
    private static ResponseEntity doDownloadRequest(RequestEntity requestEntity, boolean needHeaders) {
        HttpGet get = new HttpGet(requestEntity.getUrl());

        //设置请求头
        setHeader(get, requestEntity.getHeaders());
        CloseableHttpResponse response = executeRequest(requestEntity.getHttpClient(), get, requestEntity.getSocksProxy());
        return resultCheck(response, requestEntity.getCharset(), BYTE_RES_TYPE, needHeaders);
    }

    //执行get请求
    private static ResponseEntity doGetRequest(RequestEntity requestEntity, String resultType, boolean needHeaders) {
        String url = setUrlParams(requestEntity.getUrl(), requestEntity.getParams());
        HttpGet get = new HttpGet(url);
        //设置请求头
        setHeader(get, requestEntity.getHeaders());
        CloseableHttpResponse response = executeRequest(requestEntity.getHttpClient(), get, requestEntity.getSocksProxy());
        return resultCheck(response, requestEntity.getCharset(), resultType, needHeaders);
    }

    //执行post请求
    private static ResponseEntity doPostRequest(RequestEntity requestEntity, String resultType, boolean needHeaders) {
        HttpPost post = new HttpPost(requestEntity.getUrl());
        //设置请求头
        setHeader(post, requestEntity.getHeaders());
        setParams(post, requestEntity.getMultipartEntityBuilder(), requestEntity.getParams(), requestEntity.getParamBody(), requestEntity.getCharset());
        CloseableHttpResponse response = executeRequest(requestEntity.getHttpClient(), post, requestEntity.getSocksProxy());
        return resultCheck(response, requestEntity.getCharset(), resultType, needHeaders);
    }

    private static ResponseEntity doPutRequest(RequestEntity requestEntity, String resultType, boolean needHeaders) {
        HttpPut post = new HttpPut(requestEntity.getUrl());
        //设置请求头
        setHeader(post, requestEntity.getHeaders());
        setParams(post, requestEntity.getMultipartEntityBuilder(), requestEntity.getParams(), requestEntity.getParamBody(), requestEntity.getCharset());
        CloseableHttpResponse response = executeRequest(requestEntity.getHttpClient(), post, requestEntity.getSocksProxy());
        return resultCheck(response, requestEntity.getCharset(), resultType, needHeaders);
    }

    //设置请求头
    private static void setHeader(HttpRequestBase httpRequest, Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> current : headers.entrySet()) {
                httpRequest.addHeader(current.getKey(), current.getValue());
            }
        }
    }

    //设置url请求参数
    private static String setUrlParams(String url, Map<String, String> params) {
        StringBuilder resultUrl = new StringBuilder();
        resultUrl.append(url);
        resultUrl.append("?");
        if (params != null) {
            for (Map.Entry<String, String> current : params.entrySet()) {
                resultUrl.append(current.getKey());
                resultUrl.append("=");
                resultUrl.append(current.getValue());
                resultUrl.append("&");
            }
        }
        return resultUrl.deleteCharAt(resultUrl.length() - 1).toString();
    }

    //设置post，patch，put的请求参数
    private static void setParams(HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase, MultipartEntityBuilder multipartEntityBuilder, Map<String, String> params, String paramBody, Charset charset) {
        if (multipartEntityBuilder != null) {
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpEntityEnclosingRequestBase.setEntity(httpEntity);
        } else if (StringUtils.isEmpty(paramBody)) {
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> current : params.entrySet()) {
                    paramList.add(new BasicNameValuePair(current.getKey(), current.getValue()));
                }
                httpEntityEnclosingRequestBase.setEntity(new UrlEncodedFormEntity(paramList, charset));
            }
        } else {
            httpEntityEnclosingRequestBase.setEntity(new StringEntity(paramBody, charset));
        }
    }

    //执行请求
    private static CloseableHttpResponse executeRequest(CloseableHttpClient httpClient, HttpRequestBase httpRequest, InetSocketAddress socksProxy) {
        try {
            if (socksProxy != null) {
                HttpClientContext httpClientContext = HttpClientContext.create();
                httpClientContext.setAttribute("socks.address", socksProxy);
                return httpClient.execute(httpRequest, httpClientContext);
            } else {
                return httpClient.execute(httpRequest);
            }
        } catch (Exception e) {
            log.error("httpclient请求异常:{}", e.toString());
            return null;
        }
    }

    //包装请求结果
    private static ResponseEntity resultCheck(CloseableHttpResponse response, Charset charset, String resType, boolean needHeader) {
        try {
            if (response != null) {
                String responseCode = String.valueOf(response.getStatusLine().getStatusCode());

                switch (responseCode.substring(0, 1)) {
                    case "2": {
                        if (resType.equals(STRING_RES_TYPE)) {
                            return new ResponseEntity(responseCode, "请求成功", response.getEntity() == null ? "" : EntityUtils.toString(response.getEntity(), charset), response.getAllHeaders());
                        } else {
                            if (needHeader) {
                                return new ResponseEntity(responseCode, "请求成功", response.getEntity().getContent().readAllBytes(), response.getAllHeaders());
                            } else {
                                return new ResponseEntity(responseCode, "请求成功", response.getEntity().getContent().readAllBytes());
                            }
                        }
                    }
                    case "3": {
                        return new ResponseEntity(responseCode, "需要重定向", response.getFirstHeader("location").getValue());
                    }
                    case "4": {
                        return new ResponseEntity(responseCode, "请求无效", EntityUtils.toString(response.getEntity(), charset));
                    }
                    case "5": {
                        return new ResponseEntity(responseCode, "服务器异常", EntityUtils.toString(response.getEntity(), charset));
                    }
                    default: {
                        return new ResponseEntity(responseCode, "未知的响应码", EntityUtils.toString(response.getEntity(), charset));
                    }
                }
            } else {
                return new ResponseEntity("-100", "发送请求失败", "发送请求失败");
            }
        } catch (Exception e) {
            log.error("请求发送异常", e);
            return new ResponseEntity("-200", e.toString(), "解析返回数据发生异常");
        } finally {
            close(null, response);
        }
    }

    //关闭连接
    public static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        try {
            if (httpClient != null)
                httpClient.close();
            if (response != null)
                response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}