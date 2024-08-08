package com.shj.steam_gui.utils.http.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

@Slf4j
public class AppRetryHandler implements HttpRequestRetryHandler {

    private int maxRetryTimes;

    public AppRetryHandler(int maxRetryTimes) {
        this.maxRetryTimes = maxRetryTimes;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount > maxRetryTimes) {
            log.info("超过最大尝试次数");
            return false;
        } else if (exception instanceof SocketException) {
            if ("Connection refused: connect".equals(exception.getMessage())) {
                log.info("发生SocketException异常{}, 连接被拒，不再重试", exception.toString());
                return false;
            } else {
                log.info("发生SocketException异常{}, 开始第{}次重试", exception.toString(), executionCount);
                return true;
            }
        } else if (exception instanceof SSLProtocolException) {
            log.info("发生SSLProtocolException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else if (exception instanceof ClientProtocolException) {
            log.info("发生ClientProtocolException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else if (exception instanceof SocketTimeoutException) {
            log.info("发生SocketTimeoutException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else if (exception instanceof ConnectTimeoutException) {
            log.info("发生ConnectTimeoutException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else if (exception instanceof SSLException) {
            log.info("发生SSLException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else if (exception instanceof NoHttpResponseException) {
            log.info("发生NoHttpResponseException异常{},开始第{}次重试", exception.toString(), executionCount);
            return true;
        } else {
            log.info("不支持重试的异常:{}", exception.toString());
            return false;
        }
    }
}
