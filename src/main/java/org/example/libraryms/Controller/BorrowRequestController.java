package org.example.libraryms.Controller;

import org.example.libraryms.Common.BaseResponse;
import org.example.libraryms.Entity.User;
import org.example.libraryms.Repository.UserRepository;
import org.example.libraryms.Security.JwtUtil;
import org.example.libraryms.Service.SseService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
        String email = jwtUtil.extractClaims(token).getSubject();
        User user = userRepository.findByEmail(email);
        return sseService.subcribe(user.getId());
    }

    //@GetMapping("/v1/borrow-request")
    //public ResponseEntity<BaseResponse<Page<BRSearchResponse>>>  search(BRSearchRequest brSearchRequest, Pageable pageable){
        //Page<BRSearchResponse> data = BorrowRequestService.search(brSearchRequest, pageable);
        //return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(data,"lay request thanh cong"));
    //}

    //@PostMapping("/v1/borrow-request")
    //public ResponseEntity<BaseResponse<Void>> request()
}
