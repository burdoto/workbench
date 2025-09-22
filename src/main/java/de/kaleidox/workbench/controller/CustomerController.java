package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.repo.CustomerRepository;
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
    @Autowired UserRepository     users;
    @Autowired CustomerRepository customers;

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
        var tree = Streams.of(customers.findAll()).collect(Collectors.groupingBy(Customer::getName));

        model.addAttribute("tree", tree);

        return "customer/index";
    }
}
