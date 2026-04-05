package org.example.libraryms.Service.Impl;

import org.example.libraryms.Service.SseService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseServiceImpl implements SseService {
    private final Map<Integer, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter subcribe(Integer userId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));
        return emitter;
    }

    @Override
    public void sendToUser(Integer userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            return;
        }
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch(IOException e){
            emitters.remove(userId);
        }
    }

    @Override
    public void broadcast(String eventName, Object data) {
        List<Integer> dead = new ArrayList<>();
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch(IOException e){
                dead.add(id);
            }
        });
        dead.forEach(id -> emitters.remove(id));
    }
}
