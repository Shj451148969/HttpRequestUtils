package com.shj.steam_gui.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.Authenticator;

@Slf4j
@Order(1)
@Component
public class ApplicationInit implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Authenticator.setDefault(new AppAuthenticator());

//        log.info("加载userAgent");
//        UserAgentUtil.setUserAgents(userAgentDao.getUserAgent());
//        log.info("加载userAgent完成");

        log.info("SavePic: {}", StartConfig.isSavePic());
        log.info("SaveToDatabase: {}", StartConfig.isSaveToDatabase());
        log.info("SavePath: {}", StartConfig.getSavePath());
        log.info("DockerAddress: {}", StartConfig.getDockerAddress());
        log.info("proxyProduce:{}", StartConfig.getProxyProduce());
//        String ip = ProxyUtil.getMyIp();
//        log.info("获取到本机IP：{}", ip);
//        if (!ProxyUtil.checkWhiteListByAZ(ip)) {
//            log.info("白名单中不存在本机IP，准备添加");
//            boolean addResult = ProxyUtil.addWhiteByAZ(ip);
//            if (addResult) {
//                log.info("白名单添加成功");
//            } else {
//                log.info("白名单添加失败，退出程序");
//                System.exit(0);
//            }
//        } else {
//            log.info("白名单中在本机IP");
//        }

    }
}
