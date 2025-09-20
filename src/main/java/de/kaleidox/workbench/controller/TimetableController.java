package de.kaleidox.workbench.controller;

import de.kaleidox.workbench.model.jpa.timetable.TimetableEntry;
import de.kaleidox.workbench.repo.TimetableEntryRepository;
import de.kaleidox.workbench.repo.UserRepository;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collection;

@Controller
@RequestMapping("/timetable")
public class TimetableController {
    @Autowired UserRepository           users;
    @Autowired TimetableEntryRepository entries;

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
            display         = entries.findByCustomerNameOrderByStartTimeAsc(customer);
        } else {
            displayInfoText = "Aktuelle Kalenderwoche";
            display         = entries.findThisWeekByUser(user.getUsername());
        }

        model.addAttribute("user", user);
        model.addAttribute("entries", display);
        model.addAttribute("displayInfoText", displayInfoText);

        return "timetable/index";
    }

    @GetMapping("/create")
    public String create() {
        return "timetable/create_entry";
    }
}
