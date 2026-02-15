package com.example.is_curse_work.repository;

import com.example.is_curse_work.model.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    @Query("""
            select a from AuditLog a
            where (:actorEmail is null or lower(a.actorEmail) like lower(concat('%', :actorEmail, '%')))
              and (:action is null or a.action = :action)
            order by a.createdAt desc
            """)
    List<AuditLog> search(@Param("actorEmail") String actorEmail,
                          @Param("action") String action,
                          Pageable pageable);
}
