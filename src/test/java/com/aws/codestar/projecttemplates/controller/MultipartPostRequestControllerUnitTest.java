package com.aws.codestar.projecttemplates.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.aws.codestar.projecttemplates.configuration.MvcConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class, SimpleUpload.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class MultipartPostRequestControllerUnitTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void whenFileUploaded_thenVerifyStatus() throws Exception {
        MockMultipartFile file = new MockMultipartFile("File",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());

        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
        mockMvc.perform(multipart("/upload")
                .file(file))
                .andExpect(status().isOk());
    }
}
