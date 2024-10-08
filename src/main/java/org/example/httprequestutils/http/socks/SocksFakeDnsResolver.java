package com.shj.steam_gui.utils.http.socks;

import org.apache.http.conn.DnsResolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SocksFakeDnsResolver implements DnsResolver {

    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {
        // Return some fake DNS record for every request, we won't be using it
        return new InetAddress[]{InetAddress.getByAddress(new byte[]{1, 1, 1, 1})};
    }

}
