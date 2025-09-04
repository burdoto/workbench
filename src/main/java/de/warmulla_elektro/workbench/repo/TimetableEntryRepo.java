package de.warmulla_elektro.workbench.repo;

import de.warmulla_elektro.workbench.model.entity.TimetableEntry;
import de.warmulla_elektro.workbench.model.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface TimetableEntryRepo extends CrudRepository<TimetableEntry, UUID> {
    Collection<TimetableEntry> findByCustomerNameOrderByStartTimeAsc(String customerName);

    @Query("""
            select te from TimetableEntry te
                join fetch te.assignments assignment
                where assignment.user = :user
                  and WEEK(te.startTime) = WEEK(NOW())
                order by te.startTime asc
            """)
    Collection<TimetableEntry> findThisWeekByUser(User user);

    @Query("""
            select te from TimetableEntry te
                join fetch te.assignments assignment
                where assignment.user = :user
                  and YEAR(te.startTime) = :year
                  and WEEK(te.startTime) = :week
                order by te.startTime asc
            """)
    Collection<TimetableEntry> findWeekByUser(User user, int year, int week);
}
