package com.authservice.repositories;

import com.authservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r.name FROM Role r JOIN r.user u WHERE u.id = :userId")
    List<String> findRoleNamesByUserId(long userId);
}
