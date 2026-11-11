package com.tayjava.demo;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class MeController {

  public record WhoAmI(String method, String principal) {}

  @GetMapping("/public")
  Mono<String> open() {
    return Mono.just("anyone can call");
  }

  @GetMapping("/me/annotation")
  Mono<WhoAmI> viaAnnotation(@AuthenticationPrincipal Jwt jwt) {
    return Mono.just(new WhoAmI("@AuthenticationPrincipal", jwt.getSubject()));
  }

  @GetMapping("/me/context")
  Mono<WhoAmI> viaContext() {
    return ReactiveSecurityContextHolder.getContext()
        .map(
            ctx ->
                new WhoAmI(
                    "ReactiveSecurityContextHolder", ctx.getAuthentication().getName()))
        .switchIfEmpty(Mono.error(new AccessDeniedException("No SecurityContext")));
  }

  @GetMapping("/me/threadlocal")
  Mono<WhoAmI> viaThreadLocal() {
    var auth = SecurityContextHolder.getContext().getAuthentication(); // NEVER do this on WebFlux
    return Mono.just(
        new WhoAmI(
            "SecurityContextHolder (ThreadLocal)",
            auth == null ? "null - THIS IS THE PROBLEM" : auth.getName()));
  }
}
