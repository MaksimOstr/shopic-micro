package com.userservice.repositories;

import com.userservice.entity.User;
import com.userservice.projection.EmailVerifyProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
}
