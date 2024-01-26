package com.wen.demo;

import com.wen.spring.annotations.Autowired;
import com.wen.spring.annotations.Component;

@Component
public class OrderService {
    @Autowired
    private UserService userService;

    public void test(){
        System.out.println("----- order ------");
    }

}
