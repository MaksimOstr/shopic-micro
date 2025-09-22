package com.productservice.repository;

import com.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>, JpaSpecificationExecutor<Brand> {

    @Transactional
    @Modifying
    @Query("UPDATE Brand b SET b.isActive = :status WHERE b.id = :id")
    int changeIsActive(int id, boolean status);

    boolean existsBrandByName(String name);
}
