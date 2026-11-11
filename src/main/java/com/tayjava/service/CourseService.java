package com.tayjava.service;

import com.tayjava.common.dto.course.CourseResp;
import java.util.List;
import java.util.Optional;

public interface CourseService {

  List<CourseResp> list();

  CourseResp get(Long id);

  CourseResp create(String title, String instructorUsername);

  CourseResp update(Long courseId, String title);

  void delete(Long courseId);

  Optional<String> findInstructorUsername(Long courseId);
}
