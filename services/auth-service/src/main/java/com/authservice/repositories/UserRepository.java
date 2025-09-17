package com.authservice.repositories;

import com.authservice.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isVerified = :verified WHERE u.id = :userId")
    int markUserVerified(long userId,  boolean verified);


    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> getUserForAuth(String email);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findWithProfileAndRolesById(Long id);

    Optional<User> findByEmail(String email);
}
