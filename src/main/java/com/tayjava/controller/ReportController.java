package com.tayjava.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lesson 17: fine-grained {@code report:view} permission surface. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

  @GetMapping("/revenue")
  public ResponseEntity<Map<String, Object>> revenue() {
    return ResponseEntity.ok(Map.of("total", 0));
  }
}
