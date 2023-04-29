package com.ena.timesheet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadController {
    @GetMapping("/upload-ena")
    public String uploadFormENA() {
        return "upload-ena";
    }
    @GetMapping("/upload-phd")
    public String uploadFormPHD() {
        return "upload-phd";
    }
}