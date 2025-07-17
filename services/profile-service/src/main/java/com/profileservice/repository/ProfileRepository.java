package com.profileservice.repository;


import com.profileservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUserId(long userId);

    Optional<Profile> findByUserId(long userId);
}
