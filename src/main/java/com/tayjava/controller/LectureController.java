package com.tayjava.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Lesson 19: paid lecture list gated by {@code EnrolledCourseAuthorizationManager}. */
@RestController
@RequestMapping("/api/courses/{courseId}/lectures")
public class LectureController {

  @GetMapping
  public ResponseEntity<List<Map<String, Object>>> list(@PathVariable Long courseId) {
    return ResponseEntity.ok(
        List.of(
            Map.of("id", 1, "courseId", courseId, "title", "Welcome"),
            Map.of("id", 2, "courseId", courseId, "title", "Authorization deep dive")));
  }
}
