package com.spring_batch_3.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring_batch_3.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
