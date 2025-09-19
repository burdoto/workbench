package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.User;
import org.comroid.api.func.util.Debug;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static java.util.Optional.*;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @ApiStatus.Internal
    default Optional<User> get(Authentication auth) {
        if (auth == null) return Debug.isDebug() ? of(save(User.DEV)) : empty();
        var username = auth.getName();
        return findById(username).or(() -> Debug.isDebug() ? of(save(User.DEV)) : empty());
    }
}
