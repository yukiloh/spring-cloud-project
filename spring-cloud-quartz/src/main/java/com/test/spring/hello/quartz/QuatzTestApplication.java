package com.test.spring.hello.quartz;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class QuatzTestApplication {



    /*每10s打印一次时间*/
    @Scheduled(cron = "0/2 * * * * ?")
    public void showTime(){
        System.out.println("time: " + new SimpleDateFormat("mm:ss").format(new Date()));
    }
}
