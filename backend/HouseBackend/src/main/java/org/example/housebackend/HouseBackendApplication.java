package org.example.housebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.housebackend.mapper")
public class HouseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseBackendApplication.class, args);
    }

}
