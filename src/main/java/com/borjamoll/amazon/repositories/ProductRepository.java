package com.borjamoll.amazon.repositories;

import com.borjamoll.amazon.data.ProductDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public interface ProductRepository extends JpaRepository<ProductDB, Long> {

    @Transactional
    int deleteProductDBByName(String name);

    @Transactional(readOnly = true)
    @Query(value = "select price, date from productdb where name=:name", nativeQuery = true)
    List<String> findAllTracers(@Param("name") String name);
}