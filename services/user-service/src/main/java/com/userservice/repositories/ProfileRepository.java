package com.userservice.repositories;

import com.userservice.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    boolean existsByUser_Id(long userId);

    Optional<Profile> findByUser_Id(long userId);
}
