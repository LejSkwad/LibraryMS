package org.example.libraryms.Service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    SseEmitter subcribe(Integer userId);
    void sendToUser(Integer userId, String eventName, Object data);
    void broadcast(String eventName, Object data);
}
