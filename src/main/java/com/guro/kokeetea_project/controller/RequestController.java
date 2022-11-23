package com.guro.kokeetea_project.controller;

import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.guro.kokeetea_project.dto.RequestDetailDTO;
import com.guro.kokeetea_project.dto.RequestFormDTO;
import com.guro.kokeetea_project.dto.RequestInfoDTO;
import com.guro.kokeetea_project.service.RequestService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping(value = "/request/list")
    public String listRequest(@PathVariable("page") Optional<Integer> page, Model model, RedirectAttributes flash){
        return "request/list";
    }

    @PostMapping(value = "/request/ajax")
    public @ResponseBody ResponseEntity<String> ajaxRequest(HttpServletRequest req, HttpServletResponse res){
        try {
            Integer draw = Integer.parseInt(req.getParameter("draw"));
            Integer start = Integer.parseInt(req.getParameter("start"));
            Integer length = Integer.parseInt(req.getParameter("length"));
            Pageable pageable = PageRequest.of((int)Math.floor(start/length), length);
            Page<RequestInfoDTO> page = requestService.list(pageable);
            String data = "[";
            for (RequestInfoDTO row : page.getContent()) {
                data += "[\""+row.getId()+"\", \""+row.getDate()+"\", \""+row.getIngredientName()
                +"\", \""+row.getAmount()+"\", \""+row.getStoreName()+"\", \""+row.getWarehouseName()+"\", \""+row.getStatus()
                +"\", \""+(row.getCanConfirm()?"1":"0")+(row.getCanReject()?"1":"0")+(row.getCanCancel()?"1":"0")+"\"], ";
            }
            data = data.substring(0, data.length()-2)+"]";
            Long totalCount = page.getTotalElements();
            res.setContentType("application/json");
            String response = "{\"draw\": " + draw + ", \"recordsTotal\": " + totalCount + ", \"recordsFiltered\":" + totalCount + ", \"data\": " + data + "}";
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"error\": \"목록 표시 중 에러가 발생하였습니다.\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/request/myajax")
    public @ResponseBody ResponseEntity<String> myajaxRequest(HttpServletRequest req, HttpServletResponse res, Principal principal){
        try {
            Integer draw = Integer.parseInt(req.getParameter("draw"));
            Integer start = Integer.parseInt(req.getParameter("start"));
            Integer length = Integer.parseInt(req.getParameter("length"));
            Pageable pageable = PageRequest.of((int)Math.floor(start/length), length);
            Page<RequestInfoDTO> page = requestService.mylist(pageable, principal.getName());
            String data = "[";
            for (RequestInfoDTO row : page.getContent()) {
                data += "[\""+row.getId()+"\", \""+row.getDate()+"\", \""+row.getIngredientName()
                +"\", \""+row.getAmount()+"\", \""+row.getWarehouseName()+"\", \""+row.getStatus()
                +"\", \""+(row.getCanComplete()?"1":"0")+(row.getCanCancel()?"1":"0")+"\"], ";
            }
            data = data.substring(0, data.length()-2)+"]";
            Long totalCount = page.getTotalElements();
            res.setContentType("application/json");
            String response = "{\"draw\": " + draw + ", \"recordsTotal\": " + totalCount + ", \"recordsFiltered\":" + totalCount + ", \"data\": " + data + "}";
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("{\"error\": \"목록 표시 중 에러가 발생하였습니다.\"}", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/request/mylist")
    public String mylistRequest(Principal principal, Model model, RedirectAttributes flash){ 
        return "request/mylist";
    }

    @GetMapping(value = "/request/mycreate")
    public String createRequest(Model model, RedirectAttributes flash, Principal principal){
        try {
            model.addAttribute("requestFormDTO", requestService.prepare(principal.getName()));
            model.addAttribute("ingredients", requestService.ingredients());
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "양식 표시 중 에러가 발생하였습니다.");
            return "redirect:/request/mylist";
        }
        
        return "request/mycreate";
    }

    @PostMapping(value = "/request/mycreate")
    public String createRequestPost(@Valid RequestFormDTO requestFormDTO, BindingResult bindingResult, Model model, RedirectAttributes flash){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("ingredients", requestService.ingredients());
                return "request/mycreate";
            }
            requestService.create(requestFormDTO);
        } catch (Exception e){
            flash.addFlashAttribute("errorMessage", "발주 등록 중 에러가 발생하였습니다.");
        }
        return "redirect:/request/mylist";
    }

    @PostMapping(value = "/request/confirm/{id}")
    public @ResponseBody ResponseEntity<String> confirmRequest(@PathVariable("id") Long id) {
        try {
            requestService.confirm(id);
        } catch (Exception e) {
            requestService.error(id);
            return new ResponseEntity<>("발주 승인 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/request/mycomplete/{id}")
    public @ResponseBody ResponseEntity<String> mycompleteRequest(@PathVariable("id") Long id) {
        try {
            requestService.complete(id);
        } catch (Exception e) {
            requestService.error(id);
            return new ResponseEntity<>("배송 확인 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/request/reject/{id}")
    public @ResponseBody ResponseEntity<String> rejectRequest(@PathVariable("id") Long id) {
        try {
            requestService.reject(id);
        } catch (Exception e) {
            requestService.error(id);
            return new ResponseEntity<>("발주 반려 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/request/cancel/{id}")
    public @ResponseBody ResponseEntity<String> cancelRequest(@PathVariable("id") Long id) {
        try {
            requestService.cancel(id);
        } catch (Exception e) {
            requestService.error(id);
            return new ResponseEntity<>("발주 취소 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/request/mycancel/{id}")
    public @ResponseBody ResponseEntity<String> mycancelRequest(@PathVariable("id") Long id) {
        try {
            requestService.cancel(id);
        } catch (Exception e) {
            requestService.error(id);
            return new ResponseEntity<>("발주 취소 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @GetMapping(value = "/request/view/{id}")
    public String viewRequest(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes flash){
        try {
            RequestDetailDTO requestDetailDTO = requestService.status(id);
            model.addAttribute("request", requestDetailDTO);
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "발주 표시 중 에러가 발생하였습니다.");
            return "redirect:/request/list";
        }
        
        return "request/view";
    }

    @GetMapping(value = "/request/myview/{id}")
    public String myviewRequest(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes flash){
        try {
            RequestDetailDTO requestDetailDTO = requestService.mystatus(id);
            if (requestDetailDTO.getStoreEmail()!=principal.getName()) {
                flash.addFlashAttribute("errorMessage", "조회 권한이 없습니다.");
                return "redirect:/request/mylist";
            }
            model.addAttribute("request", requestDetailDTO);
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "발주 표시 중 에러가 발생하였습니다.");
            return "redirect:/request/mylist";
        }
        
        return "request/view";
    }
}
