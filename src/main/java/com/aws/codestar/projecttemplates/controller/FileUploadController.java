package com.aws.codestar.projecttemplates.controller;

import jakarta.servlet.annotation.MultipartConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
//@RestController
@MultipartConfig
public class FileUploadController {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @PostMapping(path = "/upload_ena_timesheet") // , consumes = "multipart/form-data"
    public ResponseEntity<String> save(@RequestParam("File") MultipartFile file) {
        // Save the file to a local directory
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        logger.info("FileUploadController:ReceivedFile: " + fileName);
        String destPath = "/tmp/" + fileName;
        try {
            file.transferTo(new File(destPath));
        } catch (IOException e) {
            logger.error("FileUploadController:ErrorSavingFile: " + fileName, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
