package com.wen.demo;

import com.wen.spring.annotations.Autowired;
import com.wen.spring.annotations.Component;
import com.wen.spring.annotations.Scope;

@Component
@Scope
public class UserService {
    @Autowired
    private OrderService orderService;

    public void test(){
        System.out.println("----- user ------");
        orderService.test();
    }

}
