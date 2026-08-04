package com.factoryops.analytics.dto.response;

import com.factoryops.analytics.entity.NotificationChannel;
import com.factoryops.analytics.entity.NotificationStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;

    private String recipient;

    private String title;

    private String message;

    private NotificationChannel channel;

    private NotificationStatus status;

    private LocalDateTime sentAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
