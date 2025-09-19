package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.jpa.representant.User;
import de.kaleidox.workbench.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class ApiController {
    @Autowired UserRepository users;

    @ResponseBody
    @GetMapping("/me")
    public User me(Authentication auth) {
        return users.get(auth).orElseThrow();
    }
}
