package com.authservice.repositories;

import com.authservice.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isVerified = :verified WHERE u.id = :userId")
    int markUserVerified(@Param("userId") UUID userId, @Param("verified") boolean verified);

    Optional<User> findByEmail(String email);
}
