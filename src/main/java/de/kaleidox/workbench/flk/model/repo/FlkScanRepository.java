package de.kaleidox.workbench.flk.model.repo;

import de.kaleidox.workbench.flk.model.entity.FlkScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlkScanRepository extends CrudRepository<FlkScan, String> {
}
