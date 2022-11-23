package com.guro.kokeetea_project.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityNotFoundException;

import com.guro.kokeetea_project.dto.*;
import com.guro.kokeetea_project.entity.CurrentStock;
import com.guro.kokeetea_project.repository.CurrentStockRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.guro.kokeetea_project.constant.RequestStatus;
import com.guro.kokeetea_project.entity.Ingredient;
import com.guro.kokeetea_project.entity.Request;
import com.guro.kokeetea_project.entity.Store;
import com.guro.kokeetea_project.entity.Warehouse;
import com.guro.kokeetea_project.repository.IngredientRepository;
import com.guro.kokeetea_project.repository.RequestRepository;
import com.guro.kokeetea_project.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final IngredientRepository ingredientRepository;
    private final StoreRepository storeRepository;
    private final CurrentStockRepository currentStockRepository;

    @Transactional(readOnly = true)
    public StatisticsDTO stat() throws Exception {
        StatisticsDTO stat = new StatisticsDTO();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastmonthStart = now.toLocalDate().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime thismonthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastyearStart = now.toLocalDate().minusMonths(12).withDayOfMonth(1).atStartOfDay();
        stat.setCountByMonth(requestRepository.countByMonth(lastyearStart, thismonthStart));
        int index = 0;
        int i = 0;
        long value = 0L;
        for (StatOfRequestByMonth x : stat.getCountByMonth()) {
            if (x.getValue() > value) {
                value = x.getValue();
                index = i;
            }
            i++;
        }
        stat.setCountByMonthMaxIndex(index);
        stat.setStatOfRequestByMonthIngredient(requestRepository.countByMonthIngredient(lastmonthStart, thismonthStart));
        stat.setStatOfRequestByMonthStore(requestRepository.countByMonthStore(lastmonthStart, thismonthStart));
        return stat;
    }

    @Transactional(readOnly = true)
    public Boolean validate(Request request) {
        if (request.getIngredient() == null || request.getAmount() == null || request.getStore() == null
        || request.getWarehouse() == null || request.getDate() == null || request.getStatus() == null) {
            return false;
        }
        if (!request.getIngredient().getIsValid() || !request.getStore().getIsValid() || !request.getWarehouse().getIsValid()) {
            return false;
        }
        if (request.getAmount() <= 0) {
            return false;
        }
        return true;
    }

    public void error(Long id) {
        Request request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            request.setStatus(RequestStatus.ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Page<RequestInfoDTO> list(Pageable pageable) throws Exception {
        List<Request> requests = requestRepository.listRequest(pageable);
        Long totalCount = requestRepository.countRequest();
        List<RequestInfoDTO> list = new ArrayList<>();

        for (Request request : requests){
            RequestInfoDTO dto = new RequestInfoDTO();
            dto.setId(request.getId());
            dto.setIngredientName(request.getIngredient()!=null?request.getIngredient().getName():null);
            dto.setAmount(request.getAmount());
            dto.setStoreName(request.getStore()!=null?request.getStore().getName():null);
            dto.setDate(request.getDate()!=null?request.getDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분").withLocale(Locale.forLanguageTag("ko"))):null);
            dto.setWarehouseName(request.getWarehouse()!=null?request.getWarehouse().getName():null);
            if (this.validate(request)) {
                switch (request.getStatus()) {
                    case PENDING : dto.setStatus("대기"); dto.setCanConfirm(true); dto.setCanReject(true); break;
                    case INPROGRESS : dto.setStatus("배송중"); dto.setCanCancel(true); break;
                    case COMPLETE : dto.setStatus("완료"); break;
                    case REJECTED : dto.setStatus("반려"); break;
                    case CANCELLED : dto.setStatus("취소"); break;
                    case NEEDACTION : dto.setStatus("확인필요"); break;
                    default : dto.setStatus("에러");
                }
            } else {
                dto.setStatus("에러");
            }
            list.add(dto);
        }

        return new PageImpl<RequestInfoDTO>(list, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public Page<RequestInfoDTO> mylist(Pageable pageable, String email) throws Exception {
        List<Request> requests = requestRepository.mylistRequest(email, pageable);
        Long totalCount = requestRepository.mycountRequest(email);
        List<RequestInfoDTO> list = new ArrayList<>();

        for (Request request : requests){
            RequestInfoDTO dto = new RequestInfoDTO();
            dto.setId(request.getId());
            dto.setIngredientName(request.getIngredient()!=null?request.getIngredient().getName():null);
            dto.setAmount(request.getAmount());
            dto.setStoreName(request.getStore()!=null?request.getStore().getName():null);
            dto.setDate(request.getDate()!=null?request.getDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분").withLocale(Locale.forLanguageTag("ko"))):null);
            dto.setWarehouseName(request.getWarehouse()!=null?request.getWarehouse().getName():null);
            if (this.validate(request)) {
                switch (request.getStatus()) {
                    case PENDING : dto.setStatus("대기"); dto.setCanCancel(true); break;
                    case INPROGRESS : dto.setStatus("배송중"); dto.setCanComplete(true); dto.setCanCancel(true); break;
                    case COMPLETE : dto.setStatus("완료"); break;
                    case REJECTED : dto.setStatus("반려"); break;
                    case CANCELLED : dto.setStatus("취소"); break;
                    case NEEDACTION : dto.setStatus("확인필요"); break;
                    default : dto.setStatus("에러");
                }
            } else {
                dto.setStatus("에러");
            }
            list.add(dto);
        }

        return new PageImpl<>(list, pageable, totalCount);
    }

    public RequestFormDTO prepare(String email) throws Exception {
        RequestFormDTO requestFormDTO = new RequestFormDTO();
        Store store = storeRepository.findByEmail(email);
        if (store == null) {
            throw new EntityNotFoundException();
        }
        requestFormDTO.setStoreId(store.getId());
        return requestFormDTO;
    }

    public Long create(RequestFormDTO requestFormDTO) throws Exception {
        Ingredient ingredient = ingredientRepository.findById(requestFormDTO.getIngredientId()).orElseThrow(EntityNotFoundException::new);
        if (!ingredient.getIsValid()) {
            throw new EntityNotFoundException();
        }
        Store store = storeRepository.findById(requestFormDTO.getStoreId()).orElseThrow(EntityNotFoundException::new);
        if (!store.getIsValid()) {
            throw new EntityNotFoundException();
        }
        Warehouse warehouse = store.getWarehouse();
        if (warehouse == null || !warehouse.getIsValid()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        CurrentStock currentStock = currentStockRepository.findValidCurrentStock(ingredient, warehouse);
        if (currentStock == null) {
            throw new EntityNotFoundException();
        }
        if (currentStock.getAmount() < requestFormDTO.getAmount()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        
        Request request = new Request();
        request.setIngredient(ingredient);
        request.setAmount(requestFormDTO.getAmount());
        request.setStore(store);
        request.setStatus(RequestStatus.PENDING);
        request.setDate(LocalDateTime.now());
        request.setWarehouse(warehouse);

        requestRepository.save(request);

        return request.getId();
    }

    public void confirm(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!this.validate(request) || request.getStatus()!=RequestStatus.PENDING) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        CurrentStock currentStock = currentStockRepository.findValidCurrentStock(request.getIngredient(), request.getWarehouse());
        if (currentStock == null) {
            throw new EntityNotFoundException();
        }
        if (currentStock.getAmount() < request.getAmount()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        currentStock.setAmount(currentStock.getAmount() - request.getAmount());
        request.setStatus(RequestStatus.INPROGRESS);
    }

    public void complete(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!this.validate(request) || request.getStatus()!=RequestStatus.INPROGRESS) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        request.setStatus(RequestStatus.COMPLETE);
    }

    public void reject(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!this.validate(request) || request.getStatus()!=RequestStatus.PENDING) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        request.setStatus(RequestStatus.REJECTED);
    }

    public void cancel(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!this.validate(request) || (request.getStatus()!=RequestStatus.PENDING && request.getStatus()!=RequestStatus.INPROGRESS)) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        if (request.getStatus()==RequestStatus.INPROGRESS) {
            CurrentStock currentStock = currentStockRepository.findValidCurrentStock(request.getIngredient(), request.getWarehouse());
            if (currentStock == null) {
                throw new EntityNotFoundException();
            }
            currentStock.setAmount(currentStock.getAmount() + request.getAmount());
        }
        request.setStatus(RequestStatus.CANCELLED);
    }

    @Transactional(readOnly = true)
    public RequestDetailDTO status(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        RequestDetailDTO requestDetailDTO = new RequestDetailDTO();
        requestDetailDTO.setId(id);
        requestDetailDTO.setSourceUrl("/request/list");
        requestDetailDTO.setDate(request.getDate()!=null?request.getDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분").withLocale(Locale.forLanguageTag("ko"))):null);
        if (this.validate(request)) {
            switch (request.getStatus()) {
                case PENDING : requestDetailDTO.setStatus("대기"); requestDetailDTO.setCanConfirm(true); requestDetailDTO.setCanReject(true); break;
                case INPROGRESS : requestDetailDTO.setStatus("배송중"); requestDetailDTO.setCanCancel(true); break;
                case COMPLETE : requestDetailDTO.setStatus("완료"); break;
                case REJECTED : requestDetailDTO.setStatus("반려"); break;
                case CANCELLED : requestDetailDTO.setStatus("취소"); break;
                case NEEDACTION : requestDetailDTO.setStatus("확인필요"); break;
                default : requestDetailDTO.setStatus("에러");
            }
        } else {
            requestDetailDTO.setStatus("에러");
        }
        requestDetailDTO.setIngredientName(request.getIngredient().getName());
        requestDetailDTO.setCategoryName(request.getIngredient().getCategory().getName());
        requestDetailDTO.setIngredientPrice(request.getIngredient().getPrice());
        requestDetailDTO.setAmount(request.getAmount());
        requestDetailDTO.setTotalPrice(request.getIngredient().getPrice()*request.getAmount());

        requestDetailDTO.setStoreName(request.getStore().getName());
        requestDetailDTO.setStoreLocation(request.getStore().getLocation());
        requestDetailDTO.setStorePhone(request.getStore().getPhone());
        requestDetailDTO.setStoreEmail(request.getStore().getEmail());

        requestDetailDTO.setWarehouseName(request.getWarehouse().getName());
        requestDetailDTO.setWarehouseLocation(request.getWarehouse().getLocation());
        requestDetailDTO.setWarehousePhone(request.getWarehouse().getPhone());
        requestDetailDTO.setWarehouseEmail(request.getWarehouse().getEmail());

        return requestDetailDTO;
    }

    @Transactional(readOnly = true)
    public RequestDetailDTO mystatus(Long id) throws Exception {
        Request request = requestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        RequestDetailDTO requestDetailDTO = new RequestDetailDTO();
        requestDetailDTO.setId(id);
        requestDetailDTO.setSourceUrl("/request/mylist");
        requestDetailDTO.setDate(request.getDate()!=null?request.getDate().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분").withLocale(Locale.forLanguageTag("ko"))):null);
        if (this.validate(request)) {
            switch (request.getStatus()) {
                case PENDING : requestDetailDTO.setStatus("대기"); requestDetailDTO.setCanCancel(true); break;
                case INPROGRESS : requestDetailDTO.setStatus("배송중"); requestDetailDTO.setCanComplete(true); requestDetailDTO.setCanCancel(true); break;
                case COMPLETE : requestDetailDTO.setStatus("완료"); break;
                case REJECTED : requestDetailDTO.setStatus("반려"); break;
                case CANCELLED : requestDetailDTO.setStatus("취소"); break;
                case NEEDACTION : requestDetailDTO.setStatus("확인필요"); break;
                default : requestDetailDTO.setStatus("에러");
            }
        } else {
            requestDetailDTO.setStatus("에러");
        }
        requestDetailDTO.setIngredientName(request.getIngredient().getName());
        requestDetailDTO.setCategoryName(request.getIngredient().getCategory().getName());
        requestDetailDTO.setIngredientPrice(request.getIngredient().getPrice());
        requestDetailDTO.setAmount(request.getAmount());
        requestDetailDTO.setTotalPrice(request.getIngredient().getPrice()*request.getAmount());

        requestDetailDTO.setStoreName(request.getStore().getName());
        requestDetailDTO.setStoreLocation(request.getStore().getLocation());
        requestDetailDTO.setStorePhone(request.getStore().getPhone());
        requestDetailDTO.setStoreEmail(request.getStore().getEmail());

        requestDetailDTO.setWarehouseName(request.getWarehouse().getName());
        requestDetailDTO.setWarehouseLocation(request.getWarehouse().getLocation());
        requestDetailDTO.setWarehousePhone(request.getWarehouse().getPhone());
        requestDetailDTO.setWarehouseEmail(request.getWarehouse().getEmail());

        return requestDetailDTO;
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
}
