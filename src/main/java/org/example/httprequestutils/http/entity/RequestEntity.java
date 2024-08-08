package com.shj.steam_gui.utils.http.entity;

import com.shj.steam_gui.utils.http.HttpClientUtils;
import com.shj.steam_gui.utils.http.HttpResult;
import com.shj.steam_gui.utils.http.handler.AppDefaultRedirectStrategy;
import com.shj.steam_gui.utils.http.handler.AppRetryHandler;
import com.shj.steam_gui.utils.http.socks.SocksConnectionSocketFactory;
import com.shj.steam_gui.utils.http.socks.SocksSSLConnectionSocketFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.apache.http.Consts.UTF_8;

@Getter
@Slf4j
public class RequestEntity implements Serializable {

    public RequestEntity() {
        this.httpResult = (request, response) -> response;
    }

    public RequestEntity(String url, String requestType) {
        this.url = url;
        this.requestType = requestType;
        this.httpResult = (request, response) -> response;
    }

    private final List<String> redirectURL = new ArrayList<>();

    //最大重试次数
    private int maxReTryTimes;

    //请求结果返回后的处理
    private HttpResult httpResult;

    //请求地址
    private String url;

    //请求类型
    private String requestType;

    //请求头
    private Map<String, String> headers;

    //请求参数
    private Map<String, String> params;

    //请求参数体
    private String paramBody;

    private MultipartEntityBuilder multipartEntityBuilder;
    //请求代理
    private HttpHost httpProxy;

    //使用socks代理
    private InetSocketAddress socksProxy;

    //返回字符集
    private Charset charset = UTF_8;

    //cookie存储
    private BasicCookieStore cookieStore = new BasicCookieStore();

    //cookie编码形式
    private String cookieSpec = CookieSpecs.STANDARD_STRICT;

    private boolean disableCookie = false;

    private String useProxyIp;

    private Integer useProxyPort;

    //请求体
    private CloseableHttpClient httpClient;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setMaxReTryTimes(int maxReTryTimes) {
        this.maxReTryTimes = maxReTryTimes;
    }

    public void setCookieSpec(String cookieSpec) {
        this.cookieSpec = cookieSpec;
    }

    public void disableCookie(boolean disable) {
        this.disableCookie = disable;
    }

    public void setMultipartEntityBuilder(MultipartEntityBuilder multipartEntityBuilder) {
        this.multipartEntityBuilder = multipartEntityBuilder;
    }

    public void addParam(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }

    public void addParam(String key, int value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, String.valueOf(value));
    }

    public void addParam(String key, long value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, String.valueOf(value));
    }

    public void setParamBody(String paramBody) {
        this.paramBody = paramBody;
    }

    public void removeParam(String key) {
        if (params != null) {
            params.remove(key);
        }
    }

    public void clearParams() {
        this.params = null;
        this.paramBody = null;
        this.multipartEntityBuilder = null;
    }

    public void addHeader(String key, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.put(key, value);
    }

    public void clearHeader() {
        if (headers == null) {
            headers = new HashMap<>();
        } else {
            headers.clear();
        }
    }

    public void removeHeader(String key) {
        if (headers != null) {
            headers.remove(key);
        }
    }

    public List<Cookie> getCookies() {
        return this.cookieStore.getCookies();
    }

    public String getCookieValue(String cookieName) {
        for (Cookie cookie : this.cookieStore.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void addCookie(String key, String value, String domain, String path, boolean secure) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(key, value);
        basicClientCookie.setDomain(domain);
        basicClientCookie.setPath(path);
        basicClientCookie.setSecure(secure);

        this.cookieStore.addCookie(basicClientCookie);
    }

    public void addCookie(String key, String value, String domain, String path, boolean secure, Date expiryDate) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(key, value);
        basicClientCookie.setDomain(domain);
        basicClientCookie.setPath(path);
        basicClientCookie.setSecure(secure);
        basicClientCookie.setExpiryDate(expiryDate);

        this.cookieStore.addCookie(basicClientCookie);
    }

    public void addCookie(String key, String value) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(key, value);
        this.cookieStore.addCookie(basicClientCookie);
    }

    public void setCookieList(List<Cookie> cookieList) {
        if (cookieList != null) {
            if (cookieStore == null) {
                this.cookieStore = new BasicCookieStore();
            }
            cookieStore.addCookies(cookieList.toArray(new Cookie[0]));
        }
    }

    public void setProxy(String ip, Integer port, ProxyTypeEnum type) {
        if (ip != null && port != null) {
            this.useProxyIp = ip;
            this.useProxyPort = port;
            switch (type) {
                case HTTP:
                    this.socksProxy = null;
                    this.httpProxy = new HttpHost(ip, port);
                    break;
                case HTTPS:
                    this.socksProxy = null;
                    this.httpProxy = new HttpHost(ip, port, "https");
                    break;
                case SOCKS:
                    this.httpProxy = null;
                    this.socksProxy = new InetSocketAddress(ip, port);
                    break;
                default:
                    log.warn("不支持的代理协议");
                    break;
            }
        }
    }

    public void removeProxy() {
        this.httpProxy = null;
        this.socksProxy = null;
    }

    public void buildHttpClient() {
        if (disableCookie) {
            this.httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(20000)
                            .setConnectionRequestTimeout(20000)
                            .setSocketTimeout(20000)
                            .setCookieSpec(this.cookieSpec)
                            .setProxy(this.httpProxy)
                            .build())
                    .setRedirectStrategy(new AppDefaultRedirectStrategy(this))
                    .disableCookieManagement()
                    .setRetryHandler(new AppRetryHandler(maxReTryTimes))
                    .setConnectionManager(createSSL())
                    .build();
        } else {
            this.httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(20000)
                            .setConnectionRequestTimeout(20000)
                            .setSocketTimeout(20000)
                            .setCookieSpec(this.cookieSpec)
                            .setProxy(this.httpProxy)
                            .build())
                    .setRedirectStrategy(new AppDefaultRedirectStrategy(this))
                    .setDefaultCookieStore(this.cookieStore)//设置Cookie
                    .setRetryHandler(new AppRetryHandler(maxReTryTimes))
                    .setConnectionManager(createSSL())
                    .build();
        }
    }

    //绕过SSL
    private HttpClientConnectionManager createSSL() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
            X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sslContext.init(null, new TrustManager[]{trustManager}, null);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", new SocksConnectionSocketFactory())
                    .register("https", new SocksSSLConnectionSocketFactory(sslContext))
                    .build();

            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } catch (Exception e) {
            log.error("创建SSL失败", e);
        }
        return null;
    }

    public void close() {
        HttpClientUtils.close(this.httpClient, null);
    }

}
