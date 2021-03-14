package com.example.boottest.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWord {

    @RequestMapping("/Na")
    @ResponseBody
    public String sayHelloWord(){
        return "Hello word!";
    }
}
