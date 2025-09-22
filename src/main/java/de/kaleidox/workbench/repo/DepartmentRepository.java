package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.Customer;
import de.kaleidox.workbench.model.jpa.representant.Department;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Repository
@RequestMapping("/api/departments")
public interface DepartmentRepository extends CrudRepository<Department, String> {
    default Department getOrCreate(String name) {
        return findById(name).orElseGet(() -> save(new Department(name)));
    }

    @Modifying
    @ResponseBody
    @Transactional
    @PostMapping("create")
    @Query(nativeQuery = true, value = "insert into department (name) values (:name);")
    void create(@Param("name") @RequestBody String name);

    default Department getOrCreateDefault(Customer customer) {
        return customer.findDepartment("").orElseGet(() -> {
            var department = new Department("");
            var persistent = save(department);
            customer.getDepartments().add(persistent);
            return persistent;
        });
    }
}
