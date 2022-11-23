package com.guro.kokeetea_project.controller;

import com.guro.kokeetea_project.dto.StatisticsDTO;
import com.guro.kokeetea_project.service.RequestService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final RequestService requestService;

    @GetMapping(value="/")
    public String main(Optional<Integer> page, Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            return "redirect:/member/login";
        }
        try {
            model.addAttribute("stat", requestService.stat());
        } catch (Exception e) {
            model.addAttribute("stat", new StatisticsDTO());
        }
        model.addAttribute("username", principal.getName());

        return "main";
    }
}