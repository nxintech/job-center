package com.nxin.framework.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by petzold on 2016/1/22.
 */
@Controller
@RequestMapping("/home")
public class HomeController
{
    @ResponseBody
    @RequestMapping(value = "/")
    public String hello()
    {
        return "hello world";
    }
}
