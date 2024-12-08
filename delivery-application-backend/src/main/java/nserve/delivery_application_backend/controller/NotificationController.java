package nserve.delivery_application_backend.controller;

import nserve.delivery_application_backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/send-notification")
    public String sendNotification(@RequestParam String userId, @RequestParam String message) {
        notificationService.sendNotificationToUser(userId, message);
        return "Notification sent to user: " + userId;
    }

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @GetMapping("/connected-users")
    public String getConnectedUsers() {
        StringBuilder users = new StringBuilder("Connected users: ");
        simpUserRegistry.getUsers().forEach(user -> {
            users.append(user.getName()).append(", ");
        });
        return users.toString();
    }
}
