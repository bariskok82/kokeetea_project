package com.guro.kokeetea_project.controller;

import com.guro.kokeetea_project.dto.CurrentStockFormDTO;
import com.guro.kokeetea_project.dto.*;
import com.guro.kokeetea_project.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @GetMapping(value = "/warehouse/list")
    public String listWarehouse(Model model, RedirectAttributes flash, SearchDTO searchDTO){
        try {
            List<WarehouseInfoDTO> warehouseList = warehouseService.list();

            model.addAttribute("warehouseFilter", warehouseService.warehouses());
            model.addAttribute("ingredientFilter", warehouseService.ingredients());
            model.addAttribute("searchDTO",searchDTO);
            model.addAttribute("warehouses", warehouseList);
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "목록 표시 중 에러가 발생하였습니다.");
            return "redirect:/";
        }
        return "warehouse/list";
    }

    @GetMapping(value = "/warehouse/create")
    public String createWarehouse(Model model){
        model.addAttribute("warehouseFormDTO", new WarehouseFormDTO());
        return "warehouse/create";
    }

    @PostMapping(value = "/warehouse/create")
    public String createWarehousePost(@Valid WarehouseFormDTO warehouseFormDTO, BindingResult bindingResult, Model model){
        try {
            if (bindingResult.hasErrors()){
                return "warehouse/create";
            }
            warehouseService.create(warehouseFormDTO);
        } catch (Exception e){
            model.addAttribute("errorMessage", "창고 등록 중 에러가 발생하였습니다.");
        }
        return "redirect:/warehouse/list";
    }

    @GetMapping(value = "/warehouse/update/{id}")
    public String updateWarehouse(@PathVariable("id") Long id, Model model, RedirectAttributes flash){
        try {
            WarehouseFormDTO warehouseFormDTO = warehouseService.original(id);
            model.addAttribute("warehouseFormDTO", warehouseFormDTO);
            return "warehouse/create";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "양식 표시 중 에러가 발생하였습니다.");
            return "redirect:/warehouse/list";
        }
    }

    @PostMapping(value = "/warehouse/update")
    public String updateWarehousePost(@Valid WarehouseFormDTO warehouseFormDTO, BindingResult bindingResult, RedirectAttributes flash){
        try {
            if (bindingResult.hasErrors()){
                return "warehouse/create";
            }
            warehouseService.update(warehouseFormDTO);
        } catch (Exception e){
            flash.addFlashAttribute("errorMessage", "창고 수정 중 에러가 발생하였습니다.");
        }
        return "redirect:/warehouse/list";
    }

    @PostMapping(value = "/warehouse/delete/{id}")
    public @ResponseBody ResponseEntity<String> deleteWarehousePost(@PathVariable("id") Long id) {
        try {
            warehouseService.delete(id);
        } catch (Exception e) {
            return new ResponseEntity<>("창고 삭제 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @PostMapping(value = "/warehouse/delete/{id}/full")
    public @ResponseBody ResponseEntity<String> deleteFullWarehousePost(@PathVariable("id") Long id) {
        try {
            warehouseService.deleteFull(id);
        } catch (Exception e) {
            return new ResponseEntity<>("창고 삭제 중 에러가 발생하였습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(String.valueOf(id), HttpStatus.OK);
    }

    @GetMapping(value = "/warehouse/view/{id}")
    public String viewWarehouse(@PathVariable("id") Long id, Model model, RedirectAttributes flash) throws Exception {
        try {
            WarehouseStockInfoDTO warehouseStockInfoDTO = warehouseService.statusWarehouse(id);
            Map<String, Long> itemsSum = warehouseService.chartWarehouse(warehouseStockInfoDTO);
            model.addAttribute("warehouse", warehouseStockInfoDTO);
            model.addAttribute("data", itemsSum);
            return "warehouse/view";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "창고 조회 중 에러가 발생하였습니다.");
            return "redirect:/warehouse/list";
        }
    }

    @PostMapping(value = "/warehouse/view/{id}/refresh")
    public String refreshWarehouse(@PathVariable("id") Long id, Model model) throws Exception {
        try {
            WarehouseStockInfoDTO warehouseStockInfoDTO = warehouseService.statusWarehouse(id);
            model.addAttribute("warehouse", warehouseStockInfoDTO);
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return "warehouse/view :: #warehouse-data";
    }

    @GetMapping(value = "/warehouse/ingredient/{id}")
    public String viewWarehouseByIngredient(@PathVariable("id") Long id, Model model, RedirectAttributes flash) throws Exception {
        try {
            IngredientStockInfoDTO ingredientStockInfoDTO = warehouseService.statusIngredient(id);
            model.addAttribute("ingredient", ingredientStockInfoDTO);
            return "warehouse/ingredient/view";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "창고 조회 중 에러가 발생하였습니다.");
            return "redirect:/warehouse/list";
        }
    }

    @PostMapping(value = "/warehouse/ingredient/{id}/refresh")
    public String refreshIngredient(@PathVariable("id") Long id, Model model) throws Exception {
        try {
            IngredientStockInfoDTO ingredientStockInfoDTO = warehouseService.statusIngredient(id);
            model.addAttribute("ingredients", ingredientStockInfoDTO);
        } catch (Exception e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }

        return "warehouse/ingredient/view :: #warehouse-data";
    }

    @GetMapping(value = {"/warehouse/currentstock/{id}", "/warehouse/currentstock/{id}/{type}"})
    public String updateCurrentStock(@PathVariable("id") Long id, @PathVariable("type") Optional<String> type, Model model, RedirectAttributes flash){
        try {
            Boolean isIngredient = type.map(s -> s.equals("ingredient")).orElse(false);
            CurrentStockFormDTO currentStockFormDTO = warehouseService.originalCurrentStock(id, isIngredient);
            model.addAttribute("currentStockFormDTO", currentStockFormDTO);
            return "warehouse/currentstock";
        } catch (Exception e) {
            flash.addFlashAttribute("errorMessage", "양식 표시 중 에러가 발생하였습니다.");
            return "redirect:/warehouse/list";
        }
    }

    @PostMapping(value = "/warehouse/currentstock")
    public String updateCurrentStockPost(@Valid CurrentStockFormDTO currentStockFormDTO, BindingResult bindingResult, RedirectAttributes flash){
        try {
            if (bindingResult.hasErrors()){
                return "warehouse/currentstock";
            }
            warehouseService.updateCurrentStock(currentStockFormDTO);
        } catch (Exception e){
            flash.addFlashAttribute("errorMessage", "재고 갱신 중 에러가 발생하였습니다.");
        }
        return "redirect:"+currentStockFormDTO.getSourceUrl();
    }
}
