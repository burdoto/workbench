package de.warmulla_elektro.workbench.repo;

import de.warmulla_elektro.workbench.model.entity.TimetableEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface EntryTable extends CrudRepository<TimetableEntry, UUID> {
    Collection<TimetableEntry> findAllByCustomerNameOrderByStartTimeAsc(String customerName);
}
