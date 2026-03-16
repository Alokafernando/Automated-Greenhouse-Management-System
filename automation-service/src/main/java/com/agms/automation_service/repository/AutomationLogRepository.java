package com.agms.automation_service.repository;

import com.agms.automation_service.entity.AutomationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutomationLogRepository extends JpaRepository<AutomationLog, Long> {
    List<AutomationLog> findAllByOrderByTimestampDesc();
}