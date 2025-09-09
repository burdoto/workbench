package de.warmulla_elektro.workbench.controller;

import de.warmulla_elektro.workbench.model.entity.TimetableEntry;
import de.warmulla_elektro.workbench.repo.CustomerRepository;
import de.warmulla_elektro.workbench.repo.TimetableEntryRepository;
import de.warmulla_elektro.workbench.repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
@RequestMapping
public class GenericController {
    @Autowired UserRepository           users;
    @Autowired CustomerRepository       customers;
    @Autowired TimetableEntryRepository entries;

    @GetMapping("/")
    public String index() {
        return "redirect:/timetable";
    }

    @GetMapping("/timetable")
    public String timetable(
            Model model, HttpSession session, @RequestParam(required = false) @Nullable Short year,
            @RequestParam(required = false) @Nullable Byte week,
            @RequestParam(required = false) @Nullable String customer
    ) {
        var                        user = users.get(session).orElseThrow();
        String displayInfoText;
        Collection<TimetableEntry> display;

        if (year == null) year = (short) LocalDateTime.now().getYear();

        if (week != null) {
            displayInfoText = "Kalenderwoche: %d in %d".formatted(week, year);
            display = entries.findWeekByUser(user.getUsername(), year, week);
        } else if (customer != null) {
            displayInfoText = "Kunde: %s".formatted(customer);
            display         = entries.findByCustomerNameOrderByStartTimeAsc(customer);
        } else {
            displayInfoText = "Aktuelle Kalenderwoche";
            display = entries.findThisWeekByUser(user.getUsername());
        }

        model.addAttribute("user", user);
        model.addAttribute("entries", display);
        model.addAttribute("displayInfoText", displayInfoText);

        return "timetable";
    }
}
