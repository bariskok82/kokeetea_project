package com.guro.kokeetea_project.controller;

import com.guro.kokeetea_project.dto.MemberFormDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/member")
@Controller
@RequiredArgsConstructor
public class MemberController {
    @GetMapping(value = "/new")
    public String newMember(Model model){
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/register";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/login";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요");
        return "member/login";
    }
}