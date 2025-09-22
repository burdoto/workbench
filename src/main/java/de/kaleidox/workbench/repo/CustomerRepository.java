package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

@Controller
@Repository
@RequestMapping("/api/customers")
public interface CustomerRepository extends CrudRepository<Customer, Customer.CompositeKey> {
    @ResponseBody
    @GetMapping("/names")
    @Query("select distinct c.name from Customer c")
    Collection<String> names();

    @ResponseBody
    @GetMapping("/{name}/departments")
    @Query("select c.department from Customer c where c.name= :name")
    Collection<String> departments(@PathVariable @Param("name") String name);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("create")
    @Query(nativeQuery = true, value = """
            insert into customer (name, department)
            values (?#{#info.name()}, ?#{#info.department()});
            """)
    void create(@Param("info") @RequestBody Customer.CompositeKey info);
}
