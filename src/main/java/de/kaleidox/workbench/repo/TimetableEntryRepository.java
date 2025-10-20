package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.timetable.Assignment;
import de.kaleidox.workbench.model.jpa.timetable.Interruption;
import de.kaleidox.workbench.model.jpa.timetable.TimetableEntry;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

@Controller
@Repository
@RequestMapping("/api/timetableEntries")
public interface TimetableEntryRepository extends CrudRepository<TimetableEntry, TimetableEntry.CompositeKey> {
    @Query("""
            select timetable_entry from TimetableEntry timetable_entry
                left join fetch timetable_entry.assignments assignment
                where (timetable_entry.createdBy.username = :username or assignment.user.username = :username)
                  and WEEK(timetable_entry.startTime) = WEEK(NOW())
                order by timetable_entry.startTime asc
            """)
    Collection<TimetableEntry> findThisWeekByUser(String username);

    @Query("""
            select timetable_entry from TimetableEntry timetable_entry
                left join fetch timetable_entry.assignments assignment
                where (timetable_entry.createdBy.username = :username or assignment.user.username = :username)
                  and YEAR(timetable_entry.startTime) = :year
                  and WEEK(timetable_entry.startTime) = :week
                order by timetable_entry.startTime asc
            """)
    Collection<TimetableEntry> findWeekByUser(String username, short year, byte week);

    @Query("""
            select timetable_entry from TimetableEntry timetable_entry
                where timetable_entry.customer.name = :customerName
                  and (:departmentName is null or timetable_entry.department.name = :departmentName)
                order by timetable_entry.startTime asc
            """)
    Collection<TimetableEntry> findCustomer(@NotNull String customerName, @Nullable String departmentName);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("create")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry (customer_name, department_name, start_time, end_time, notes, created_by_username)
                values (?#{#data.customerName()}, ?#{#data.departmentName()},
                                    ?#{#data.startTime()}, ?#{#data.endTime()}, ?#{#data.notes()}, ?#{#auth.name});
            """)
    void create(@Param("auth") Authentication auth, @Param("data") @RequestBody TimetableEntry.CreateData data);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("edit")
    @Query(value = """
            update timetable_entry
                set end_time = ?#{#data.newEndTime()},
                    notes = ?#{#data.newNotes()}
                where customer_name = ?#{#data.customerName()}
                    and department_name = ?#{#data.departmentName()}
                    and start_time = ?#{#data.startTime()}
            """, nativeQuery = true)
    void edit(@Param("data") @RequestBody TimetableEntry.EditData data);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("createInterruption")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_interruptions (timetable_entry_customer_name, timetable_entry_department_name, timetable_entry_start_time,
                        time, duration, created_by_username)
                values (?#{#data.entryInfo().customerName()},
                                    ?#{#data.entryInfo().departmentName()},
                                    ?#{#data.entryInfo().startTime()},
                                    ?#{#data.time()}, ?#{#data.duration()}, ?#{#auth.name});
            """)
    void createInterruption(
            @Param("auth") Authentication auth, @Param("data") @RequestBody Interruption.CreateData data);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("createAssignment")
    @Query(nativeQuery = true, value = """
            insert into timetable_entry_assignments(timetable_entry_customer_name, timetable_entry_department_name, timetable_entry_start_time,
                        user_username, start_time, end_time, notes, created_by_username)
                values (?#{#data.entryInfo().customerName()},
                                    ?#{#data.entryInfo().departmentName()},
                                    ?#{#data.entryInfo().startTime()},
                                    ?#{#data.username()}, ?#{#data.startTime()}, ?#{#data.endTime()}, ?#{#data.notes()},
                                    ?#{#auth.name});
            """)
    void createAssignment(@Param("auth") Authentication auth, @Param("data") @RequestBody Assignment.CreateData data);
}
