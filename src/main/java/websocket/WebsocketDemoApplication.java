package websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class WebsocketDemoApplication {

    public static void main (String[] args) {
        SpringApplication.run (WebsocketDemoApplication.class, args);
        log.warn ("开机启动成功 --》");
    }

}
