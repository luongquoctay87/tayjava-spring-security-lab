package com.tayjava.controller;

import com.tayjava.common.dto.course.CourseResp;
import com.tayjava.service.CourseService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

  private final CourseService courseService;

  @GetMapping
  public ResponseEntity<List<CourseResp>> list() {
    return ResponseEntity.ok(courseService.list());
  }

  @GetMapping("/{id}")
  public ResponseEntity<CourseResp> get(@PathVariable Long id) {
    return ResponseEntity.ok(courseService.get(id));
  }

  @PostMapping
  public ResponseEntity<CourseResp> create(
      @RequestBody(required = false) Map<String, String> body, Authentication authentication) {
    String title =
        body != null && body.get("title") != null ? body.get("title") : "New Course";
    return ResponseEntity.status(201).body(courseService.create(title, authentication.getName()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CourseResp> update(
      @PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
    String title = body != null && body.get("title") != null ? body.get("title") : "Updated Course";
    return ResponseEntity.ok(courseService.update(id, title));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    courseService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
