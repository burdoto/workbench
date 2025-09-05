package de.warmulla_elektro.workbench.repo;

import de.warmulla_elektro.workbench.model.entity.User;
import jakarta.servlet.http.HttpSession;
import org.comroid.api.func.util.Debug;
import org.jetbrains.annotations.ApiStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @ApiStatus.Internal
    default Optional<User> get(HttpSession session) {
        if (Debug.isDebug()) {
            var dev = User.DEV;
            return Optional.of(findById(dev.getUsername()).isEmpty() ? save(dev) : dev);
        }
        var oAuth2User = ((OAuth2User) ((SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication()
                .getPrincipal());
        var username = Objects.requireNonNull(oAuth2User.<String>getAttribute("username"), "User ID cannot be null");
        return findById(username);
    }
}
