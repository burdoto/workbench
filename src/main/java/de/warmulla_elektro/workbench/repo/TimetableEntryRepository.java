package de.warmulla_elektro.workbench.repo;

import de.warmulla_elektro.workbench.model.entity.TimetableEntry;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

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
    @Transactional
    @PutMapping("createInterruption")
    @Query(nativeQuery = true, value = """
            insert into workbench_timetable_interruptions (timetable_entry_id, time, duration)
            values (:entryId, :time, :duration);
            """)
    void createInterruption(
            @RequestParam @NotNull UUID entryId, @RequestParam @NotNull LocalDateTime time,
            @RequestParam @NotNull Duration duration
    );

    @Modifying
    @Transactional
    @PutMapping("createAssignment")
    @Query(nativeQuery = true, value = """
            insert into workbench_timetable_assignments (timetable_entry_id, user_username, notes)
            values (:entryId, :username, :notes);
            """)
    void createAssignment(
            @RequestParam @NotNull UUID entryId,
            @RequestParam @NotNull String username,
            @RequestParam @Nullable String notes
    );
}
