package com.swapper.monolith.ItemService.controller;

import com.swapper.monolith.ItemService.service.CoverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cover")
public class CoverController {

    CoverService coverService;
//    @GetMapping
//    public ResponseEntity<String> getCoverUrl(@RequestParam String gameId){
//        return coverService.getCoverUrls()
//    }
}
