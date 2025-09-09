package de.warmulla_elektro.workbench.controller;

import de.warmulla_elektro.workbench.model.entity.User;
import de.warmulla_elektro.workbench.repo.CustomerRepository;
import de.warmulla_elektro.workbench.repo.TimetableEntryRepository;
import de.warmulla_elektro.workbench.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class ApiController {
    @Autowired UserRepository           users;
    @Autowired CustomerRepository       customers;
    @Autowired TimetableEntryRepository entries;

    @ResponseBody
    @GetMapping("/me")
    public User me(HttpSession session) {
        return users.get(session).orElseThrow();
    }
}
