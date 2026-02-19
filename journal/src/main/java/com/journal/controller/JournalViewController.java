package com.journal.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for rendering Thymeleaf templates.
 * Handles non-REST endpoints for the web UI.
 */
@Controller
@Slf4j
public class JournalViewController {

    /**
     * Renders the main journal page.
     *
     * @return The journal template name
     */
    @GetMapping({"/", "/journal"})
    public String journal() {
        log.debug("Rendering journal page");
        return "journal";
    }
}
