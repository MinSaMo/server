package com.konkuk.gp.controller.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/dashboard")
    public String adminPage() {
        return "admin-page";
    }

    @GetMapping("/log")
    public String logPage() {
        return "log";
    }

    @GetMapping("/userinfo")
    public String userInfoLogPage() {
        return "user-info";
    }
}
