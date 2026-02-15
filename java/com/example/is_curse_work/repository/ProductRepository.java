package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Use native query and cast the PostgreSQL enum column to text so we can compare with strings
    @Query(value = """
            select p.* from products p
            join users o on p.owner_id = o.user_id
            join zones z on p.zone_id = z.zone_id
            join fridges f on z.fridge_id = f.fridge_id
            where (:ownerEmail is null or lower(o.email) like lower(concat('%', :ownerEmail, '%')))
              and (:status is null or p.status::text = :status)
              and (:fridgeId is null or f.fridge_id = :fridgeId)
            order by p.placed_at desc
            """, nativeQuery = true)
    List<Product> searchAdmin(@Param("ownerEmail") String ownerEmail,
                              @Param("status") String status,
                              @Param("fridgeId") Long fridgeId);

    @Query(value = "select count(*) from products p where (:status is null or p.status::text = :status)", nativeQuery = true)
    long countByStatus(@Param("status") String status);
}
