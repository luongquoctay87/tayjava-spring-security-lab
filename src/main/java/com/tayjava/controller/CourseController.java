package com.tayjava.controller;

import com.tayjava.common.dto.course.CourseResp;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

  @GetMapping
  public ResponseEntity<List<CourseResp>> list() {
    return ResponseEntity.ok(
        List.of(
            new CourseResp(1L, "Spring Security Fundamentals", "spring-security-fundamentals")));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseResp> get(@PathVariable Long id) {
    return ResponseEntity.ok(
        new CourseResp(id, "Spring Security Fundamentals", "spring-security-fundamentals"));
  }

  @PostMapping
  public ResponseEntity<CourseResp> create() {
    return ResponseEntity.status(201).body(new CourseResp(2L, "New Course", "new-course"));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseResp> update(@PathVariable Long id) {
    return ResponseEntity.ok(new CourseResp(id, "Updated Course", "updated-course"));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return ResponseEntity.noContent().build();
  }
}
