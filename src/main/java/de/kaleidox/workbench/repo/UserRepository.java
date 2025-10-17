package de.kaleidox.workbench.repo;

import de.kaleidox.workbench.model.jpa.representant.User;
import org.comroid.api.func.util.Debug;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.*;

@Controller
@Repository
@RequestMapping("/api/users")
public interface UserRepository extends CrudRepository<User, String> {
    @ResponseBody
    @GetMapping("/names")
    @Query("select u.username from User u")
    Collection<String> names();

    @ApiStatus.Internal
    default Optional<User> get(Authentication auth) {
        if (auth == null) return Debug.isDebug() ? of(save(User.DEV)) : empty();
        var username = auth.getName();
        return findById(username).or(() -> of(save(Debug.isDebug() ? User.DEV : new User(username, username))));
    }
}
