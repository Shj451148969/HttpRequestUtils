package com.shj.steam_gui.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


public class AppAuthenticator extends Authenticator {

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return CommonThreadLocal.getSocks5Auth();
    }

}
