package parapharmacie.para.services;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import parapharmacie.para.entities.FileChangeEvent;
@Service
public class WebSocketNotificationService {

    public final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyFileChange(String filePath, String type, String action) {
        FileChangeEvent event = new FileChangeEvent(
                filePath,
                type,
                action,
                System.currentTimeMillis()
        );
        messagingTemplate.convertAndSend("/topic/fileChanges", event);
    }
}