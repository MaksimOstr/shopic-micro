package com.authservice.repositories;

import com.authservice.entity.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {
}
