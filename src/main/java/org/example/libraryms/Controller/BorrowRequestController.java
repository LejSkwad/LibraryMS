package org.example.libraryms.Controller;

import org.example.libraryms.Entity.User;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Security.JwtUtil;
import org.example.libraryms.Service.SseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class BorrowRequestController {
    private final JwtUtil jwtUtil;
    private final SseService sseService;
    private final UserRepository userRepository;


    public BorrowRequestController(JwtUtil jwtUtil, SseService sseService ,UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.sseService = sseService;
        this.userRepository = userRepository;
    }

    @GetMapping("/events")
    public SseEmitter events(@RequestParam String token){
        //validate token with JwtUtil
        jwtUtil.validateToken(token);
        String socialNumber = jwtUtil.extractClaims(token).getSubject();
        User user = userRepository.findBySocialNumber(socialNumber);
        return sseService.subcribe(user.getId());
    }

}
