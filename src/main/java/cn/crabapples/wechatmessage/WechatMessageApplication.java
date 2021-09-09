package cn.crabapples.wechatmessage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("cn.crabapples.wechatmessage.mapper")
public class WechatMessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(WechatMessageApplication.class, args);
    }

}
