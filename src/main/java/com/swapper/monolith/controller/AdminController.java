package com.swapper.monolith.controller;

import com.swapper.monolith.ChatService.dtos.MessageDto;
import com.swapper.monolith.service.AdminService;
import com.swapper.monolith.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    @PostMapping("/trade/delete/{order_id}")
    public ResponseEntity<MessageDto> deleteMessage(@RequestParam(name="order_id") String orderId){
        return ResponseEntity.ok(null);
    }
}
