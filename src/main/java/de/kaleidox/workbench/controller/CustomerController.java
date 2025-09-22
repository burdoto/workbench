package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.repo.CustomerRepository;
import de.kaleidox.workbench.repo.DepartmentRepository;
import de.kaleidox.workbench.repo.UserRepository;
import org.comroid.api.func.util.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired UserRepository       users;
    @Autowired CustomerRepository   customers;
    @Autowired DepartmentRepository departments;

    @ModelAttribute("users")
    public UserRepository users() {
        return users;
    }

    @ModelAttribute("customers")
    public CustomerRepository customers() {
        return customers;
    }

    @GetMapping
    public String index(Model model) {
        var tree = Streams.of(departments.findAll())
                .collect(Collectors.groupingBy(dept -> dept.getCustomer().getName()));

        model.addAttribute("tree", tree);

        return "customer/index";
    }
}
