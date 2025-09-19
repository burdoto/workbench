package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.timetable.Assignment;
import de.kaleidox.workbench.model.jpa.timetable.Interruption;
import de.kaleidox.workbench.model.jpa.timetable.TimetableEntry;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static de.kaleidox.workbench.util.ApplicationContextProvider.*;

@Controller
@Repository
@RequestMapping("/api/timetableEntries")
public interface TimetableEntryRepository extends CrudRepository<TimetableEntry, UUID> {
    Collection<TimetableEntry> findByCustomerNameOrderByStartTimeAsc(String customerName);

    @Query("""
            select te from TimetableEntry te
                join fetch te.assignments assignment
                where assignment.user.username = :username
                  and WEEK(te.startTime) = WEEK(NOW())
                order by te.startTime asc
            """)
    Collection<TimetableEntry> findThisWeekByUser(String username);

    @Query("""
            select te from TimetableEntry te
                join fetch te.assignments assignment
                where assignment.user.username = :username
                  and YEAR(te.startTime) = :year
                  and WEEK(te.startTime) = :week
                order by te.startTime asc
            """)
    Collection<TimetableEntry> findWeekByUser(String username, int year, int week);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("create")
    default TimetableEntry create(HttpSession session, @RequestBody TimetableEntry.CreateData createData) {
        var id   = UUID.randomUUID();
        var user = bean(UserRepository.class).get(session).orElseThrow();

        create(id,
                createData.customerName(),
                createData.startTime(),
                createData.endTime(),
                createData.notes(),
                user.getUsername());
        return findById(id).orElseThrow();
    }

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            insert into timetable_entry (id, customer_name, start_time, end_time, notes, created_by_username)
            values (:entryId, :customerName, :startTime, :endTime, :notes, :creator);
            """)
    void create(
            @NotNull UUID entryId, @NotNull String customerName, @NotNull LocalDateTime startTime,
            @NotNull LocalDateTime endTime, @Nullable String notes, @NotNull String creator
    );

    @Modifying
    @ResponseBody
    @PostMapping("createInterruption")
    default Interruption createInterruption(
            HttpSession session, @RequestParam @NotNull UUID entryId, @RequestParam @NotNull LocalDateTime time,
            @RequestParam @NotNull Duration duration
    ) {
        var user  = bean(UserRepository.class).get(session).orElseThrow();
        var entry = findById(entryId).orElseThrow();
        return findInterruption();
    }

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_interruptions (timetable_entry_id, time, duration, created_by_username)
            values (:entryId, :username, :notes, }, :creator);
            """)
    void createInterruption(
            @NotNull UUID entryId, @NotNull LocalDateTime time, @NotNull Duration duration,
            @NotNull String creator
    );

    @Query("""
            select i from TimetableEntry e
                inner join e.interruptions i on i.time = :time and i.duration = :duration and i.createdBy.username = :creator
                where e.id = :entryId
            """)
    Optional<Interruption> findInterruption(
            @NotNull UUID entryId, @NotNull LocalDateTime time,
            @NotNull Duration duration, @NotNull String creator
    );

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("createAssignment")
    default Assignment createAssignment(
            HttpSession session, @RequestParam @NotNull UUID entryId, @RequestParam @NotNull String username,
            @RequestParam(required = false) @Nullable String notes
    ) {}

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_assignments (timetable_entry_id, user_username, notes)
            values (:entryId, :username, :notes);
            """)
    void createAssignment(
            @RequestParam @NotNull UUID entryId, @RequestParam @NotNull String username,
            @RequestParam(required = false) @Nullable String notes
    );
}
