package com.agms.zone_management_service.repository;

import com.agms.zone_management_service.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
}
