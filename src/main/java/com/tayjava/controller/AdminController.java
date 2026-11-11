package com.tayjava.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** Lesson 16: web admin area — URL layer requires ROLE_ADMIN. */
@Controller
@RequestMapping("/admin")
public class AdminController {

  @GetMapping
  @ResponseBody
  public Map<String, String> dashboard() {
    return Map.of("area", "admin", "status", "ok");
  }
}
