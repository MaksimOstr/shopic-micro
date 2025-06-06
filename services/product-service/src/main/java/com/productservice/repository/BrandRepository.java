package com.productservice.repository;

import com.productservice.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Brand b WHERE b.id = :id")
    int deleteBrandById(int id);

    boolean existsBrandByName(String name);
}
