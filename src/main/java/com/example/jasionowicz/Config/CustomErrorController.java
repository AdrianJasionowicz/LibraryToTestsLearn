package com.example.jasionowicz.Config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null) {
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "403";
            } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "404";
            }
        }
        return "error";
    }
}
