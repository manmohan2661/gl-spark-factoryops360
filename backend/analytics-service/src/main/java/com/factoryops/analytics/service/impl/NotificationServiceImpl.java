package com.factoryops.analytics.service.impl;

import com.factoryops.analytics.dto.request.NotificationRequest;
import com.factoryops.analytics.dto.response.NotificationResponse;
import com.factoryops.analytics.entity.Notification;
import com.factoryops.analytics.entity.NotificationStatus;
import com.factoryops.analytics.exception.BusinessException;
import com.factoryops.analytics.exception.ResourceNotFoundException;
import com.factoryops.analytics.mapper.NotificationMapper;
import com.factoryops.analytics.repository.NotificationRepository;
import com.factoryops.analytics.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String RESOURCE_NAME = "Notification";

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse create(NotificationRequest request) {

        validateStatusTransition(request.getStatus(), request.getSentAt());

        Notification notification = notificationMapper.toEntity(request);

        LocalDateTime now = LocalDateTime.now();

        applySentAtConsistency(notification, now);

        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);

        try {
            Notification saved = notificationRepository.save(notification);
            return notificationMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to create notification.");
        }
    }

    @Override
    public NotificationResponse getById(Long id) {

        Notification notification = findNotificationOrThrow(id);

        return notificationMapper.toResponse(notification);
    }

    @Override
    public List<NotificationResponse> getAll() {

        return notificationMapper.toResponseList(notificationRepository.findAll());
    }

    @Override
    public NotificationResponse update(Long id, NotificationRequest request) {

        validateStatusTransition(request.getStatus(), request.getSentAt());

        Notification notification = findNotificationOrThrow(id);

        notification.setRecipient(request.getRecipient());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setChannel(request.getChannel());
        notification.setStatus(request.getStatus());
        notification.setSentAt(request.getSentAt());

        LocalDateTime now = LocalDateTime.now();

        applySentAtConsistency(notification, now);
        notification.setUpdatedAt(now);

        try {
            Notification updated = notificationRepository.save(notification);
            return notificationMapper.toResponse(updated);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Unable to update notification.");
        }
    }

    @Override
    public void delete(Long id) {

        Notification notification = findNotificationOrThrow(id);

        try {
            notificationRepository.delete(notification);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException(
                    "Cannot delete notification with id "
                            + id
                            + " because it is referenced by other records.");
        }
    }

    /**
     * Keeps sentAt consistent with status.
     */
    private void applySentAtConsistency(Notification notification,
                                        LocalDateTime now) {

        if (notification.getStatus() == NotificationStatus.SENT
                && notification.getSentAt() == null) {
            notification.setSentAt(now);
        }

        if (notification.getStatus() != NotificationStatus.SENT) {
            notification.setSentAt(null);
        }
    }

    /**
     * Validates request consistency before saving.
     */
    private void validateStatusTransition(NotificationStatus status,
                                          LocalDateTime sentAt) {

        if (status == NotificationStatus.PENDING && sentAt != null) {
            throw new BusinessException(
                    "Pending notification cannot have sentAt timestamp.");
        }

        if (status == NotificationStatus.FAILED && sentAt != null) {
            throw new BusinessException(
                    "Failed notification cannot have sentAt timestamp.");
        }
    }

    private Notification findNotificationOrThrow(Long id) {

        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(RESOURCE_NAME, id));
    }
}