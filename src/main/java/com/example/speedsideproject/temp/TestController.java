package com.example.speedsideproject.temp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RestController
public class TestController {


    @GetMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response) {

//        System.out.println(request.getHeader("access_token"));
//        System.out.println(request.getHeader("auth"));



        return "test";
    }
}
