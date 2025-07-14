package com.userservice.config;

import com.userservice.config.security.model.CustomPrincipal;
import com.userservice.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuditorAware implements AuditorAware<User> {

    private final EntityManager entityManager;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomPrincipal userDetails) {
            Long userId = userDetails.getId();
            User proxy = entityManager.getReference(User.class, userId);
            return Optional.of(proxy);
        }

        return Optional.empty();
    }
}
