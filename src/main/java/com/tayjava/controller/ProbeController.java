package com.tayjava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbeController {

  @GetMapping("/api/probe")
  public String probe() {
    return "ok";
  }
}
