package com.aws.codestar.projecttemplates.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class EnaInvoice { // THIS IS NOT USED
    @GetMapping(value="/computeEnaInvoice_THIS_IS_NOT_USED")
    public ModelAndView forwardWithParams(HttpServletRequest request) {
        request.setAttribute("param1", "one");
        request.setAttribute("param2", "two");
        return new ModelAndView("forward:/ena/invoice.html");
    }
}
