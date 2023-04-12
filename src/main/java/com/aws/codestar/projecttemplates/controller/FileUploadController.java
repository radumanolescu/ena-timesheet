package com.aws.codestar.projecttemplates.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;

@Controller
@RequestMapping(value = "ena/upload", method = RequestMethod.POST)
@CrossOrigin("*")
@MultipartConfig(location = "/tmp", fileSizeThreshold = 4194304,
        maxFileSize = 5242880,       // 5 MB
        maxRequestSize = 20971520)   // 20 MB
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostMapping(path = "ena-timesheet", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> save(@RequestParam("File") MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String contentType = file.getContentType();
        long size = file.getSize();
        logger.info("FileUploadController:ReceivedFile: " + fileName);
        logger.info("FileUploadController:ReceivedFile: " + contentType);
        logger.info("FileUploadController:ReceivedFile: " + size);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
