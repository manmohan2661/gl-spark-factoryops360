package com.factoryops.analytics.repository;


import com.factoryops.analytics.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DashboardRepository
        extends JpaRepository<Dashboard, Long> {


}