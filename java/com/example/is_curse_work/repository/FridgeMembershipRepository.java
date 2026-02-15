package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.FridgeMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FridgeMembershipRepository extends JpaRepository<FridgeMembership, FridgeMembership.PK> {
    List<FridgeMembership> findByFridgeIdAndLeftAtIsNull(Long fridgeId);
    boolean existsByFridgeIdAndUserIdAndLeftAtIsNull(Long fridgeId, Long userId);
    List<FridgeMembership> findByUserIdAndLeftAtIsNull(Long userId);
}
