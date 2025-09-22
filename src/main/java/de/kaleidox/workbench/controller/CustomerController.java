package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.jpa.representant.Department;
import de.kaleidox.workbench.repo.CustomerRepository;
import de.kaleidox.workbench.repo.DepartmentRepository;
import de.kaleidox.workbench.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

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
        final var tree = new HashMap<String, List<String>>();
        customers.findAll()
                .forEach(customer -> tree.put(customer.getName(),
                        customer.getDepartments().stream().map(Department::getName).toList()));

        model.addAttribute("tree", tree);

        return "customer/index";
    }
}
