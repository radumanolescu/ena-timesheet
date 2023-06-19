package com.ena.timesheet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {
    @GetMapping("/home")
    public String uploadFormENA() {
        return "index";
    }

    @GetMapping("/validate")
    public String validateENA() {
        return "validate";
    }

    @GetMapping("/download-all")
    public String download() {
        return "download";
    }

    @GetMapping("/error-phd")
    public String errorPHD() {
        return "error-phd";
    }

    @GetMapping("/error-ena")
    public String errorENA() {
        return "error-ena";
    }
}
