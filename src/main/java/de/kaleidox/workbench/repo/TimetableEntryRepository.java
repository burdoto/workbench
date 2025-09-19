package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.timetable.Assignment;
import de.kaleidox.workbench.model.jpa.timetable.Interruption;
import de.kaleidox.workbench.model.jpa.timetable.TimetableEntry;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ResponseBody
    @Transactional
    @PostMapping("create")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry (id, customer_name, start_time, end_time, notes, created_by_username)
            values (UUID(), ?#{#data.customerName()}, ?#{#data.startTime()}, ?#{#data.endTime()}, ?#{#data.notes()}, ?#{#auth.name});
            """)
    void create(@Param("auth") Authentication auth, @Param("data") @RequestBody TimetableEntry.CreateData data);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("createInterruption")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_interruptions (timetable_entry_id, time, duration, created_by_username)
            values (?#{#data.entryId()}, ?#{#data.time()}, ?#{#data.duration()}, ?#{#auth.name});
            """)
    void createInterruption(
            @Param("auth") Authentication auth, @Param("data") @RequestBody Interruption.CreateData data);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("createAssignment")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_assignments (timetable_entry_id, user_username, notes, created_by_username)
            values (?#{#data.entryId()}, ?#{#data.username()}, ?#{#data.notes()}, ?#{#auth.name});
            """)
    void createAssignment(@Param("auth") Authentication auth, @Param("data") @RequestBody Assignment.CreateData data);
}
