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

import static de.kaleidox.workbench.util.ApplicationContextProvider.*;

@Controller
@Repository
@RequestMapping("/api/customers")
public interface CustomerRepository extends CrudRepository<Customer, String> {
    @ResponseBody
    @GetMapping("/names")
    @Query("select c.name from Customer c")
    Collection<String> names();

    @ResponseBody
    @GetMapping("/{customerName}/departments")
    @Query("select d.name from Customer c right join c.departments d where c.name = :customerName")
    Collection<String> departments(@PathVariable("customerName") @Param("customerName") String customerName);

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("create")
    @Query(nativeQuery = true, value = "insert into customer (name) values (:name);")
    void create(@Param("name") @RequestBody String name);

    @ResponseBody
    @Transactional
    @PostMapping("/{customerName}/departments")
    default void addDepartment(@PathVariable("customerName") String customerName, @RequestBody String departmentName) {
        var department = bean(DepartmentRepository.class).getOrCreate(departmentName);
        var customer   = findById(customerName).orElseThrow();

        customer.getDepartments().add(department);
    }
}
