package com.factoryops.analytics.repository;

import com.factoryops.analytics.entity.Notification;
import com.factoryops.analytics.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countByStatus(NotificationStatus status);

}