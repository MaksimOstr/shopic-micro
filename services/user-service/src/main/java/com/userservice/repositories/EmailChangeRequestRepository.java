package com.userservice.repositories;

import com.userservice.entity.EmailChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {

}
