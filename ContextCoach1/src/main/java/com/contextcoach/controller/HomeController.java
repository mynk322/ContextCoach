package com.contextcoach.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Serves the home page
     * 
     * @return The name of the template to render
     */
    @GetMapping("/")
    public String home() {
        logger.info("Serving home page");
        return "index";
    }
}
