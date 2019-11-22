package org.wlgzs.agro_achievement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//@ServletComponentScan
@org.springframework.boot.autoconfigure.SpringBootApplication
public class AgroAchievementApplication  {
//extends SpringBootServletInitializer
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        return application.sources(AgroAchievementApplication.class);
//    }

    public static void main(String[] args) {
        SpringApplication.run(AgroAchievementApplication.class, args);
    }

}

