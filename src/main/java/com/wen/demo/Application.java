package com.wen.demo;

import com.wen.spring.EasySpringApplicationContext;
import com.wen.spring.annotations.ComponentScan;

@ComponentScan("com.wen.demo")
public class Application {

    public static void main(String[] args) {
        EasySpringApplicationContext easySpringApplicationContext = new EasySpringApplicationContext(Application.class);

    }
}
