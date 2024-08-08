package com.shj.steam_gui.utils.http.socks;

import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class SocksSSLConnectionSocketFactory extends SSLConnectionSocketFactory {

    public SocksSSLConnectionSocketFactory(final SSLContext sslContext) {
        // You may need this verifier if target site's certificate is not secure
        super(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        InetSocketAddress socks = (InetSocketAddress) context.getAttribute("socks.address");
        if (socks == null) {
            //socks代理不存在，退回普通的请求
            return SocketFactory.getDefault().createSocket();
        } else {
            //进行socks代理请求
            return new Socket(new Proxy(Proxy.Type.SOCKS, socks));
        }
    }

    @Override
    public Socket connectSocket(int connectTimeout,
                                Socket socket,
                                HttpHost host,
                                InetSocketAddress remoteAddress,
                                InetSocketAddress localAddress,
                                HttpContext context) throws IOException {
        InetSocketAddress socks = (InetSocketAddress) context.getAttribute("socks.address");
        if (socks != null) {
            // Convert address to unresolved
            remoteAddress = InetSocketAddress.createUnresolved(host.getHostName(), remoteAddress.getPort());
        }
        return super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context);
    }

}
