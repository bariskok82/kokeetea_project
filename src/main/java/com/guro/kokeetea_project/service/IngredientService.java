package com.guro.kokeetea_project.service;

import com.guro.kokeetea_project.dto.*;
import com.guro.kokeetea_project.entity.Category;
import com.guro.kokeetea_project.entity.CurrentStock;
import com.guro.kokeetea_project.entity.Ingredient;
import com.guro.kokeetea_project.entity.Warehouse;
import com.guro.kokeetea_project.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final CurrentStockRepository currentStockRepository;
    private final WarehouseRepository warehouseRepository;
    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public List<IngredientInfoDTO> list() throws Exception {
        List<Ingredient> ingredients = ingredientRepository.listIngredient();
        List<IngredientInfoDTO> list = new ArrayList<>();

        for (Ingredient ingredient : ingredients){
            IngredientInfoDTO dto = new IngredientInfoDTO();
            dto.setId(ingredient.getId());
            dto.setName(ingredient.getName());
            dto.setPrice(ingredient.getPrice());
            if (ingredient.getCategory()!=null && ingredient.getCategory().getIsValid()) {
                dto.setCategoryName(ingredient.getCategory().getName());
            } else {
                dto.setCategoryName("미배정");
            }
            list.add(dto);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public IngredientFormDTO original(Long id) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!ingredient.getIsValid()) {
            throw new EntityNotFoundException();
        }
        IngredientFormDTO dto = new IngredientFormDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setPrice(ingredient.getPrice());
        if (ingredient.getCategory()!=null && ingredient.getCategory().getIsValid()) {
            dto.setCategoryId(ingredient.getCategory().getId());
        }

        return dto;
    }

    public Long create(IngredientFormDTO ingredientFormDTO) throws Exception {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientFormDTO.getName());
        ingredient.setPrice(ingredientFormDTO.getPrice());
        ingredient.setCategory(null);
        if (ingredientFormDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(ingredientFormDTO.getCategoryId()).orElse(null);
            if (category!=null && category.getIsValid()) {
                ingredient.setCategory(category);
            }
        }
        ingredient.setIsValid(true);
        ingredientRepository.save(ingredient);

        List<Warehouse> warehouses = warehouseRepository.listWarehouse();
        for (Warehouse warehouse : warehouses) {
            CurrentStock currentStock = new CurrentStock();
            currentStock.setIngredient(ingredient);
            currentStock.setAmount(0L);
            currentStock.setWarehouse(warehouse);
            currentStockRepository.save(currentStock);
        }

        return ingredient.getId();
    }

    public Long update(IngredientFormDTO ingredientFormDTO) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(ingredientFormDTO.getId()).orElseThrow(EntityNotFoundException::new);
        if (!ingredient.getIsValid()) {
            throw new EntityNotFoundException();
        }
        ingredient.setName(ingredientFormDTO.getName());
        ingredient.setPrice(ingredientFormDTO.getPrice());
        ingredient.setCategory(null);
        if (ingredientFormDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(ingredientFormDTO.getCategoryId()).orElse(null);
            if (category!=null && category.getIsValid()) {
                ingredient.setCategory(category);
            }
        }
        ingredientRepository.save(ingredient);

        return ingredient.getId();
    }

    public void deleteFull(Long id) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        currentStockRepository.deleteByIngredient(ingredient);
        requestRepository.deleteByIngredient(ingredient);
        ingredientRepository.deleteById(id);
    }

    public void delete(Long id) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        ingredient.setIsValid(false);
    }

    @Transactional(readOnly = true)
    public List<CategoryInfoDTO> categories() throws Exception {
        List<Category> categories = categoryRepository.listCategory();
        List<CategoryInfoDTO> list = new ArrayList<>();

        if (categories.isEmpty()) {
            throw new EntityNotFoundException();
        }

        for (Category category : categories) {
            CategoryInfoDTO dto = new CategoryInfoDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            list.add(dto);
        }

        return list;
    }
}
