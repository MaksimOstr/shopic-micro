package com.authservice.repositories;

import com.authservice.entity.User;
import com.authservice.projection.user.EmailVerifyProjection;
import com.authservice.projection.user.ResetPasswordProjection;
import com.authservice.projection.user.UserEmailAndPasswordProjection;
import com.authservice.projection.user.UserForBanProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    Optional<UserEmailAndPasswordProjection> findEmailAndPasswordById(long id);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isVerified = true WHERE u.id = :userId")
    int markUserVerified(long userId);

    @Query("SELECT u.id as id, u.isVerified as verified FROM User u WHERE u.email = :email")
    Optional<EmailVerifyProjection> findUserForEmailVerify(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> getUserForAuth(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findWithProfileAndRolesById(Long id);

    @Query("SELECT r.name FROM User u JOIN u.roles r WHERE u.id = :id")
    Optional<Set<String>> getUserRoleNames(long id);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id as id, u.authProvider as authProvider FROM User u WHERE u.email = :email")
    Optional<ResetPasswordProjection> findUserForResetPassword(String email);

    @Query("SELECT new com.authservice.projection.user.UserForBanProjection(" +
            "u.isVerified," +
            "u.email" +
            ")" +
            "FROM User u WHERE u.id = :id")
    Optional<UserForBanProjection> findUserForBan(long id);
}
