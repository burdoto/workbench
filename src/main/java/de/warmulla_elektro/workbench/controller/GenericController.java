package de.warmulla_elektro.workbench.controller;

import de.warmulla_elektro.workbench.model.ErrorInfo;
import de.warmulla_elektro.workbench.repo.UserRepo;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping
public class GenericController {
    @Autowired UserRepo users;

    @GetMapping("/")
    public String index() {
        return "redirect:/timetable";
    }

    @GetMapping("/timetable")
    public String timetable(Model model, HttpSession session) {
        model.addAttribute("user", users.get(session).orElseThrow());
        return "timetable";
    }

    @GetMapping("/error")
    public String error(Model model, HttpServletRequest request) {
        var ex   = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        int code = (int) Objects.requireNonNullElse(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE), 500);
        var uri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .map(Object::toString)
                .orElse(null);
        var msg = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_MESSAGE))
                .map(Object::toString)
                .orElse(null);
        model.addAttribute("error", ErrorInfo.create(ex, code, uri, msg));
        return "error";
    }
}
