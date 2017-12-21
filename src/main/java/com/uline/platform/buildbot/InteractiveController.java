package com.uline.platform.buildbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InteractiveController {
    final Logger logger = LoggerFactory.getLogger(CommandController.class);

    @PostMapping("/buildbot/interactive")
    public String processInteractiveMessage(@RequestBody String commandRequest) {
        return commandRequest;
    }
}
