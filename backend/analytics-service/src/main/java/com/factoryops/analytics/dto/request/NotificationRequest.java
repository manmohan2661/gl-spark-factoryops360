package com.factoryops.analytics.dto.request;

import com.factoryops.analytics.entity.NotificationChannel;
import com.factoryops.analytics.entity.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NotificationRequest {

    @NotBlank(message = "Recipient is required")
    @jakarta.validation.constraints.Size(max = 150)
    private String recipient;

    @NotBlank
    private String title;

    @NotBlank
    private String message;

    @NotNull
    private NotificationChannel channel;

    @NotNull
    private NotificationStatus status;

    private LocalDateTime sentAt;
}
