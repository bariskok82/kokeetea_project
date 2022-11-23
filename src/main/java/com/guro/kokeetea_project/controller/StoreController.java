package com.guro.kokeetea_project.controller;

import com.guro.kokeetea_project.dto.StoreFormDTO;
import com.guro.kokeetea_project.dto.StoreInfoDTO;
import com.guro.kokeetea_project.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping(value = "/store/list")
    public String listStore(Model model, RedirectAttributes flash){
        try {
            List<StoreInfoDTO> storeList = storeService.list();
            model.addAttribute("stores", storeList);
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "목록 표시 중 에러가 발생하였습니다.");
            return "redirect:/";
        }

        return "store/list";
    }

    @GetMapping(value = "/store/create")
    public String createStore(Model model, RedirectAttributes flash){
        try {
            model.addAttribute("storeFormDTO", new StoreFormDTO());
            model.addAttribute("warehouses", storeService.warehouses());
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "양식 표시 중 에러가 발생하였습니다.");
            return "redirect:/store/list";
        }

        return "store/create";
    }

    @PostMapping(value = "/store/create")
    public String createStorePost(@Valid StoreFormDTO storeFormDTO, BindingResult bindingResult, Model model, RedirectAttributes flash){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("warehouses", storeService.warehouses());
                return "store/create";
            }
            storeService.create(storeFormDTO);
        } catch (Exception e){
            flash.addFlashAttribute("errorMessage", "가맹점 등록 중 에러가 발생하였습니다.");
        }
        return "redirect:/store/list";
    }

    @GetMapping(value = "/store/update/{id}")
    public String updateStore(@PathVariable("id") Long id, Model model, RedirectAttributes flash){
        try {
            StoreFormDTO storeFormDTO = storeService.original(id);
            model.addAttribute("storeFormDTO", storeFormDTO);
            model.addAttribute("warehouses", storeService.warehouses());
            return "store/create";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "양식 표시 중 에러가 발생하였습니다.");
            return "redirect:/store/list";
        }
    }

    @PostMapping(value = "/store/update")
    public String updateStorePost(@Valid StoreFormDTO storeFormDTO, BindingResult bindingResult, Model model, RedirectAttributes flash){
        try {
            if (bindingResult.hasErrors()){
                model.addAttribute("warehouses", storeService.warehouses());
                return "store/create";
            }
            storeService.update(storeFormDTO);
        } catch (Exception e){
            flash.addFlashAttribute("errorMessage", "가맹점 수정 중 에러가 발생하였습니다.");
        }
        return "redirect:/store/list";
    }

    @PostMapping(value = "/store/delete/{id}")
    public @ResponseBody ResponseEntity<String> deleteStorePost(@PathVariable("id") Long id) {
        try {
            storeService.delete(id);
        } catch (Exception e) {
            return new ResponseEntity<>("가맹점 삭제 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/store/delete/{id}/full")
    public @ResponseBody ResponseEntity<String> deleteFullStorePost(@PathVariable("id") Long id) {
        try {
            storeService.deleteFull(id);
        } catch (Exception e) {
            return new ResponseEntity<>("가맹점 삭제 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }
}
