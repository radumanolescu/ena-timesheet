package com.aws.codestar.projecttemplates.controller;

import jakarta.servlet.annotation.MultipartConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@MultipartConfig
public class SimpleUpload {
    @PostMapping(path = "/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("File") MultipartFile file) {
        return file.isEmpty() ?
                new ResponseEntity<String>(HttpStatus.NOT_FOUND) : new ResponseEntity<String>(HttpStatus.OK);
    }

}
