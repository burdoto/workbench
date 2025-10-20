package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.jpa.timetable.TimetableEntry;
import de.kaleidox.workbench.repo.CustomerRepository;
import de.kaleidox.workbench.repo.DepartmentRepository;
import de.kaleidox.workbench.repo.TimetableEntryRepository;
import de.kaleidox.workbench.repo.UserRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
@RequestMapping("/timetable")
public class TimetableController {
    @Autowired UserRepository           users;
    @Autowired CustomerRepository   customers;
    @Autowired DepartmentRepository departments;
    @Autowired TimetableEntryRepository entries;

    @ModelAttribute("users")
    public UserRepository users() {
        return users;
    }

    @ModelAttribute("customers")
    public CustomerRepository customers() {
        return customers;
    }

    @ModelAttribute("entry")
    @SuppressWarnings("MVCPathVariableInspection")
    private TimetableEntry findEntry(
            @PathVariable(value = "customerName", required = false) String customerName,
            @PathVariable(value = "departmentName", required = false) String departmentName,
            @PathVariable(value = "startTime", required = false) LocalDateTime startTime
    ) {
        if (customerName == null) return null;

        var customer = customers.findById(customerName).orElseThrow();
        var department = customer.findDepartment(departmentName)
                .orElseGet(() -> departments.getOrCreateDefault(customer));

        var eKey = new TimetableEntry.CompositeKey(customer, department, startTime);
        return entries.findById(eKey).orElseThrow();
    }

    @GetMapping
    public String index(
            Model model, Authentication auth, @RequestParam(required = false) @Nullable Short year,
            @RequestParam(required = false) @Nullable Byte week,
            @RequestParam(required = false) @Nullable String customer
    ) {
        var                        user = users.get(auth).orElseThrow();
        String                     displayInfoText;
        Collection<TimetableEntry> display;

        if (year == null) year = (short) LocalDateTime.now().getYear();

        if (week != null) {
            displayInfoText = "Kalenderwoche: %d in %d".formatted(week, year);
            display         = entries.findWeekByUser(user.getUsername(), year, week);
        } else if (customer != null) {
            displayInfoText = "Kunde: %s".formatted(customer);
            display = entries.findCustomer(customer, null);
        } else {
            displayInfoText = "Aktuelle Kalenderwoche";
            display         = entries.findThisWeekByUser(user.getUsername());
        }

        model.addAttribute("entries", display);
        model.addAttribute("displayInfoText", displayInfoText);

        return "timetable/index";
    }

    @GetMapping("/create")
    public String create() {
        return "timetable/create";
    }

    @GetMapping("/{customerName}/{departmentName}/{startTime}")
    public String view() {
        return "timetable/view";
    }

    @GetMapping("/{customerName}/{departmentName}/{startTime}/assignment/create")
    public String createAssignment() {
        return "timetable/assignment/create";
    }

    @GetMapping("/{customerName}/{departmentName}/{startTime}/interruption/create")
    public String createInterruption() {
        return "timetable/interruption/create";
    }
}
