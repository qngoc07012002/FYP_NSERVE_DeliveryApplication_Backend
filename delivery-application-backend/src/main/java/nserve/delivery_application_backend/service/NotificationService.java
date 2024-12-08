package nserve.delivery_application_backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(String userId, String message) {
        // Gửi tin nhắn tới queue riêng của user
        log.info("Sending notification to user: {}", userId);
        messagingTemplate.convertAndSend(
                "/queue/notifications/" + userId, // Queue dành riêng cho user
                message                 // Nội dung tin nhắn
        );
    }
}
