package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.ProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductHistoryRepository extends JpaRepository<ProductHistory, Long> {
    List<ProductHistory> findByProduct_ProductIdOrderByCreatedAtDesc(Long productId);
}

