package com.pacta.pacta_app.landlord.infrastructure.controller;

import com.pacta.pacta_app.landlord.application.LandlordService;
import com.pacta.pacta_app.landlord.application.dto.LandlordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/landlords")
@RequiredArgsConstructor
public class LandlordController {

    private final LandlordService landlordService;

    @GetMapping
    public List<LandlordResponse> listAll() {
        return landlordService.findAll().stream().map(LandlordResponse::from).toList();
    }

    @GetMapping("/{id}")
    public LandlordResponse getById(@PathVariable String id) {
        return LandlordResponse.from(landlordService.findById(id));
    }
}
