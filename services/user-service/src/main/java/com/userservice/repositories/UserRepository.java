package com.userservice.repositories;

import com.userservice.entity.User;
import com.userservice.projection.EmailVerifyProjection;
import com.userservice.projection.ResetPasswordProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isVerified = true WHERE u.id = :userId")
    int markUserVerified(long userId);

    @Query("SELECT u.id as id, u.isVerified as verified FROM User u WHERE u.email = :email")
    Optional<EmailVerifyProjection> findUserForEmailVerify(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> getUserForAuth(String email);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :id")
    Optional<Set<String>> getUserRoleNames(long id);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id as id, u.authProvider as authProvider FROM User u WHERE u.email = :email")
    Optional<ResetPasswordProjection> findUserForResetPassword(String email);
}
