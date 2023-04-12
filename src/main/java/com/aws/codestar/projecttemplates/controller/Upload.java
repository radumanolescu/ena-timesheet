package com.aws.codestar.projecttemplates.controller;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Upload {
    //private static final Logger logger = LoggerFactory.getLogger(Upload.class);

    @PostMapping("/ena/upload/ena-timesheet")
    @ResponseBody
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        printFileInfo(file);
    }

    @PostMapping("/ena/upload-multiple-files")
    @ResponseBody
    public List<String> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.stream(files)
                .map(file -> printFileInfo(file))
                .collect(Collectors.toList());
    }

    private String printFileInfo(MultipartFile file){
        String name = file.getOriginalFilename();
        String contentType = file.getContentType();
        long size = file.getSize();
        System.out.println("UploadController:ReceivedFile: " + name);
        System.out.println("UploadController:ReceivedFile: " + contentType);
        System.out.println("UploadController:ReceivedFile: " + size);
        return name;
    }
}
