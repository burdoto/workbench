package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {
}
