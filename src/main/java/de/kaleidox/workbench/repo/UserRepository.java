package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.User;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @ApiStatus.Internal
    default Optional<User> get(HttpSession session) {
        return get(((SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication());
    }

    @ApiStatus.Internal
    default Optional<User> get(Authentication auth) {
        if (auth == null) return Optional.empty();
        return findById(auth.getName());
    }
}
