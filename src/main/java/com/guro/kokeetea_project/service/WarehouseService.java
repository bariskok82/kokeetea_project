package com.guro.kokeetea_project.service;

import com.guro.kokeetea_project.dto.CurrentStockFormDTO;
import com.guro.kokeetea_project.dto.*;
import com.guro.kokeetea_project.entity.*;
import com.guro.kokeetea_project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseService {
    private final CurrentStockRepository currentStockRepository;
    private final IngredientRepository ingredientRepository;
    private final RequestRepository requestRepository;
    private final WarehouseRepository warehouseRepository;


    @Transactional(readOnly = true)
    public List<WarehouseInfoDTO> list() throws Exception {
        List<Warehouse> warehouses = warehouseRepository.listWarehouse();
        List<WarehouseInfoDTO> list = new ArrayList<>();

        for (Warehouse warehouse : warehouses){
            WarehouseInfoDTO warehouseInfoDTO = new WarehouseInfoDTO();
            warehouseInfoDTO.setId(warehouse.getId());
            warehouseInfoDTO.setName(warehouse.getName());
            warehouseInfoDTO.setLocation(warehouse.getLocation());
            warehouseInfoDTO.setPhone(warehouse.getPhone());
            warehouseInfoDTO.setEmail(warehouse.getEmail());
            list.add(warehouseInfoDTO);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public WarehouseFormDTO original(Long id) throws Exception {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!warehouse.getIsValid()) {
            throw new EntityNotFoundException();
        }
        WarehouseFormDTO dto = new WarehouseFormDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setPhone(warehouse.getPhone());
        dto.setEmail(warehouse.getEmail());

        return dto;
    }

    public Long create(WarehouseFormDTO warehouseFormDTO) throws Exception {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseFormDTO.getName());
        warehouse.setLocation(warehouseFormDTO.getLocation());
        warehouse.setPhone(warehouseFormDTO.getPhone());
        warehouse.setEmail(warehouseFormDTO.getEmail());
        warehouse.setIsValid(true);
        warehouseRepository.save(warehouse);

        List<Ingredient> ingredients = ingredientRepository.listIngredient();
        for (Ingredient ingredient : ingredients) {
            CurrentStock currentStock = new CurrentStock();
            currentStock.setIngredient(ingredient);
            currentStock.setAmount(0L);
            currentStock.setWarehouse(warehouse);
            currentStockRepository.save(currentStock);
        }

        return warehouse.getId();
    }

    public Long update(WarehouseFormDTO warehouseFormDTO) throws Exception {
        Warehouse warehouse = warehouseRepository.findById(warehouseFormDTO.getId()).orElseThrow(EntityNotFoundException::new);
        if (!warehouse.getIsValid()) {
            throw new EntityNotFoundException();
        }
        warehouse.setName(warehouseFormDTO.getName());
        warehouse.setLocation(warehouseFormDTO.getLocation());
        warehouse.setPhone(warehouseFormDTO.getPhone());
        warehouse.setEmail(warehouseFormDTO.getEmail());
        warehouseRepository.save(warehouse);

        return warehouse.getId();
    }

    public void deleteFull(Long id) throws Exception {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        currentStockRepository.deleteByWarehouse(warehouse);
        requestRepository.deleteByWarehouse(warehouse);
        for (Store store : List.copyOf(warehouse.getStore())) {
            store.setWarehouse(null);
        }
        warehouseRepository.deleteById(id);
    }

    public void delete(Long id) throws Exception {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        for (Store store : List.copyOf(warehouse.getStore())) {
            store.setWarehouse(null);
        }
        warehouse.setIsValid(false);
    }

    public CurrentStockFormDTO originalCurrentStock(Long id, Boolean isIngredient) throws Exception {
        CurrentStock currentStock = currentStockRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!currentStock.getWarehouse().getIsValid() || !currentStock.getIngredient().getIsValid()) {
            throw new EntityNotFoundException();
        }

        CurrentStockFormDTO currentStockFormDTO = new CurrentStockFormDTO();
        currentStockFormDTO.setId(currentStock.getId());
        currentStockFormDTO.setIngredientName(currentStock.getIngredient().getName());
        currentStockFormDTO.setAmount(currentStock.getAmount());
        if (isIngredient) {
            currentStockFormDTO.setSourceUrl("/warehouse/ingredient/"+currentStock.getIngredient().getId());
        } else {
            currentStockFormDTO.setSourceUrl("/warehouse/view/"+currentStock.getWarehouse().getId());
        }

        return currentStockFormDTO;
    }

    public Long updateCurrentStock(CurrentStockFormDTO currentStockFormDTO) throws Exception {
        CurrentStock currentStock = currentStockRepository.findById(currentStockFormDTO.getId()).orElseThrow(EntityNotFoundException::new);
        if (!currentStock.getWarehouse().getIsValid() || !currentStock.getIngredient().getIsValid()) {
            throw new EntityNotFoundException();
        }
        currentStock.setAmount(currentStockFormDTO.getAmount());
        currentStockRepository.save(currentStock);

        return currentStock.getId();
    }

    @Transactional(readOnly = true)
    public WarehouseStockInfoDTO statusWarehouse(Long id) throws Exception {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!warehouse.getIsValid()) {
            throw new EntityNotFoundException();
        }

        WarehouseStockInfoDTO warehouseStockInfoDTO = new WarehouseStockInfoDTO();
        warehouseStockInfoDTO.setId(warehouse.getId());
        warehouseStockInfoDTO.setName(warehouse.getName());

        List<CurrentStock> currentStocks = currentStockRepository.listValidCurrentStock(warehouse);
        List<CurrentStockInfoDTO> currentStockInfoDTOList = new ArrayList<>();

        for(CurrentStock currentStock : currentStocks) {
            CurrentStockInfoDTO currentStockInfoDTO = new CurrentStockInfoDTO();
            currentStockInfoDTO.setId(currentStock.getId());
            currentStockInfoDTO.setName(currentStock.getIngredient().getName());
            currentStockInfoDTO.setAmount(currentStock.getAmount());
            currentStockInfoDTOList.add(currentStockInfoDTO);
        }
        warehouseStockInfoDTO.setCurrentStockInfoDTOList(currentStockInfoDTOList);

        return warehouseStockInfoDTO;
    }


    @Transactional(readOnly = true)
    public IngredientStockInfoDTO statusIngredient(Long id) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!ingredient.getIsValid()) {
            throw new EntityNotFoundException();
        }

        IngredientStockInfoDTO ingredientStockInfoDTO = new IngredientStockInfoDTO();
        ingredientStockInfoDTO.setId(ingredient.getId());
        ingredientStockInfoDTO.setName(ingredient.getName());

        List<CurrentStock> currentStocks = currentStockRepository.listValidCurrentStock(ingredient);
        List<CurrentStockInfoDTO> currentStockInfoDTOList = new ArrayList<>();

        for (CurrentStock currentStock : currentStocks){
            CurrentStockInfoDTO currentStockInfoDTO = new CurrentStockInfoDTO();
            currentStockInfoDTO.setId(currentStock.getId());
            currentStockInfoDTO.setName(currentStock.getWarehouse().getName());
            currentStockInfoDTO.setAmount(currentStock.getAmount());
            currentStockInfoDTOList.add(currentStockInfoDTO);
        }
        ingredientStockInfoDTO.setCurrentStockInfoDTOList(currentStockInfoDTOList);

        return ingredientStockInfoDTO;
    }

    @Transactional(readOnly = true)
    public List<WarehouseStockInfoDTO> dtoList(){
        List<Warehouse> warehouses = warehouseRepository.listWarehouse();
        List<WarehouseStockInfoDTO> warehouseStockInfoDTOList = new ArrayList<>();

        for (Warehouse warehouse : warehouses) {
            if (!warehouse.getIsValid()) {
                throw new EntityNotFoundException();
            }

            WarehouseStockInfoDTO warehouseStockInfoDTO = new WarehouseStockInfoDTO();
            warehouseStockInfoDTO.setId(warehouse.getId());
            warehouseStockInfoDTO.setName(warehouse.getName());

            List<CurrentStock> currentStocks = currentStockRepository.listValidCurrentStock(warehouse);
            List<CurrentStockInfoDTO> currentStockInfoDTOList = new ArrayList<>();

            for (CurrentStock currentStock : currentStocks) {
                CurrentStockInfoDTO currentStockInfoDTO = new CurrentStockInfoDTO();
                currentStockInfoDTO.setId(currentStock.getId());
                currentStockInfoDTO.setName(currentStock.getIngredient().getName());
                currentStockInfoDTO.setAmount(currentStock.getAmount());
                currentStockInfoDTOList.add(currentStockInfoDTO);
            }

            warehouseStockInfoDTO.setCurrentStockInfoDTOList(currentStockInfoDTOList);
            warehouseStockInfoDTOList.add(warehouseStockInfoDTO);
        }

        return warehouseStockInfoDTOList;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> chartWarehouse(WarehouseStockInfoDTO _warehouseStockInfoDTO){
        List<WarehouseStockInfoDTO> dtoList = dtoList();
        Map<String, Long> itemsSum = new TreeMap<>();

        for(WarehouseStockInfoDTO warehouseStockInfoDTO : dtoList){
            if(warehouseStockInfoDTO.getId() == _warehouseStockInfoDTO.getId()){
                for(CurrentStockInfoDTO dto : warehouseStockInfoDTO.getCurrentStockInfoDTOList()) {
                    itemsSum.put(dto.getName(), dto.getAmount());
                }
            }
        }

        return itemsSum;
    }

    @Transactional(readOnly = true)
    public List<IngredientInfoDTO> ingredients() throws Exception {
        List<Ingredient> ingredients = ingredientRepository.listIngredient();
        List<IngredientInfoDTO> list = new ArrayList<>();

        if (ingredients.isEmpty()) {
            throw new EntityNotFoundException();
        }

        for (Ingredient ingredient : ingredients) {
            IngredientInfoDTO dto = new IngredientInfoDTO();
            dto.setId(ingredient.getId());
            dto.setName(ingredient.getName());
            list.add(dto);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public List<WarehouseStockInfoDTO> warehouses() throws Exception {
        List<Warehouse> warehouses = warehouseRepository.listWarehouse();
        List<WarehouseStockInfoDTO> list = new ArrayList<>();

        if (warehouses.isEmpty()) {
            throw new EntityNotFoundException();
        }

        for (Warehouse warehouse : warehouses) {
            WarehouseStockInfoDTO dto = new WarehouseStockInfoDTO();
            dto.setId(warehouse.getId());
            dto.setName(warehouse.getName());
            list.add(dto);
        }

        return list;
    }

}
