package com.shj.steam_gui.config;

import com.shj.steam_gui.utils.proxy_netty.ProxyServer;
import com.shj.steam_gui.utils.proxy_netty.SteamHubProxyServer;

import java.net.PasswordAuthentication;

public class CommonThreadLocal {

    private static ThreadLocal<PasswordAuthentication> socks5AuthThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<String> userAgentThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<String> steamHostThreadLocal = new ThreadLocal<>();

    private static ThreadLocal<ProxyServer> proxyServerThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<SteamHubProxyServer> steamHubProxyServerThreadLocal = new ThreadLocal<>();

    public static PasswordAuthentication getSocks5Auth() {
        return socks5AuthThreadLocal.get();
    }

    public static void setSocks5Auth(PasswordAuthentication passwordAuthentication) {
        socks5AuthThreadLocal.set(passwordAuthentication);
    }

    public static void setUserAgent(String userAgent) {
        userAgentThreadLocal.set(userAgent);
    }

    public static String getUserAgent() {
        return userAgentThreadLocal.get();
    }

    public static void setSteamHostThreadLocal(String host) {
        steamHostThreadLocal.set(host);
    }

    public static String getSteamHostThreadLocal() {
        return steamHostThreadLocal.get();
    }

    public static void setProxyServer(ProxyServer proxyServer) {
        proxyServerThreadLocal.set(proxyServer);
    }

    public static ProxyServer getProxyServer() {
        return proxyServerThreadLocal.get();
    }

    public static SteamHubProxyServer getSteamHubProxyServerThreadLocal() {
        return steamHubProxyServerThreadLocal.get();
    }

    public static void setSteamHubProxyServerThreadLocal(SteamHubProxyServer steamHubProxyServer) {
        steamHubProxyServerThreadLocal.set(steamHubProxyServer);
    }
}
