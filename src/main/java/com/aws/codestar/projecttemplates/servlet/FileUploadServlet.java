package com.aws.codestar.projecttemplates.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "fileUploadServlet", urlPatterns = {"/upload-ena-ts"})
@MultipartConfig(location="/tmp", fileSizeThreshold = 4194304,
        maxFileSize = 5242880,       // 5 MB
        maxRequestSize = 20971520)   // 20 MB
public class FileUploadServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            logger.warn("Part: " + part.getName() + " " + part.getContentType() + " " + part.getSize());
            if (part.getName().startsWith("File")) {
                String fileName = getFileName(part);
                logger.warn("FoundFile: " + fileName + " in " + getName(part));
                part.write("/tmp/" + fileName);
            }
        }
    }

    /**
     * Utility method to get file name from HTTP header content-disposition
     * See https://www.digitalocean.com/community/tutorials/servlet-3-file-upload-multipartconfig-part
     */
    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        logger.debug("content-disposition header = " + contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }

    private String getName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("name")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
