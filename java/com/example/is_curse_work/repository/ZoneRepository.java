package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByFridge_FridgeIdOrderBySortOrderAsc(Long fridgeId);

    @Query("""
            select z.zoneId, z.name, f.name, count(p)
            from Zone z
            join z.fridge f
            left join Product p on p.zone = z
            group by z.zoneId, z.name, f.name
            order by count(p) desc
            """)
    List<Object[]> findTopZonesByProductCount();
}
