package com.ray.hc_spring2;

import com.ray.hc_spring2.core.SocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//(exclude = DataSourceAutoConfiguration.class) not config datasource to test
@SpringBootApplication
public class HcSpring2Application {

    public static void main(String[] args) {
        SpringApplication.run(HcSpring2Application.class, args);
        SocketServer server = new SocketServer();
        server.startSocketServer(10010);
    }

}
