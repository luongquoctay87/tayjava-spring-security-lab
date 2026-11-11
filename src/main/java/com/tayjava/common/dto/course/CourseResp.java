package com.tayjava.common.dto.course;

import java.io.Serializable;

/** Minimal course view for early Security lessons (in-memory stub). */
public record CourseResp(Long id, String title, String slug) implements Serializable {}
