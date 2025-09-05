package de.warmulla_elektro.workbench.controller;

import de.warmulla_elektro.workbench.model.ErrorInfo;
import de.warmulla_elektro.workbench.model.entity.Interruption;
import de.warmulla_elektro.workbench.model.entity.TimetableEntry;
import de.warmulla_elektro.workbench.model.entity.WorkerAssignment;
import de.warmulla_elektro.workbench.repo.CustomerRepository;
import de.warmulla_elektro.workbench.repo.TimetableEntryRepository;
import de.warmulla_elektro.workbench.repo.UserRepository;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

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
            display         = entries.findWeekByUser(user, year, week);
        } else if (customer != null) {
            displayInfoText = "Kunde: %s".formatted(customer);
            display         = entries.findByCustomerNameOrderByStartTimeAsc(customer);
        } else {
            displayInfoText = "Aktuelle Kalenderwoche";
            display         = entries.findThisWeekByUser(user);
        }

        model.addAttribute("user", user);
        model.addAttribute("entries", display);
        model.addAttribute("displayInfoText", displayInfoText);

        return "timetable";
    }

    @PutMapping("/timetable")
    public String timetable(
            HttpSession session, @RequestParam @NotNull String customerName,
            @RequestParam @NotNull LocalDateTime startTime, @RequestParam @NotNull LocalDateTime endTime,
            @RequestParam @NotNull String interruptions, @RequestParam @NotNull String notes,
            @RequestParam @NotNull String assignments
    ) {
        var user     = users.get(session).orElseThrow();
        var customer = customers.findById(customerName).orElseThrow();
        var entry = new TimetableEntry().setCustomer(customer)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setInterruptions(Arrays.stream(interruptions.split(";")).map(Interruption::parse).toList())
                .setNotes(notes)
                .setAssignments(Arrays.stream(assignments.split(";")).map(WorkerAssignment::parse).toList())
                .setCreatedBy(user);

        entries.save(entry);

        return "redirect:/timetable";
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
