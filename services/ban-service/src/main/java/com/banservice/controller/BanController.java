package com.banservice.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bans")
@PreAuthorize("hasRole('ADMIN')")
public class BanController {
}
