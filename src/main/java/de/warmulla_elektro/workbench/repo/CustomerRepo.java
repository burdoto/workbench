package de.warmulla_elektro.workbench.repo;

import de.warmulla_elektro.workbench.model.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends CrudRepository<Customer, String> {
}
