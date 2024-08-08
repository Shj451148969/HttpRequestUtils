package com.shj.steam_gui.utils.http.socks;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

public class SocksConnectionSocketFactory extends PlainConnectionSocketFactory {

    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        InetSocketAddress socketAddress = (InetSocketAddress) context.getAttribute("socks.address");
        if (socketAddress == null) {
            //socks代理不存在，退回普通的请求
            return new Socket();
        } else {
            //进行socks代理请求
            return new Socket(new Proxy(Proxy.Type.SOCKS, socketAddress));
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
