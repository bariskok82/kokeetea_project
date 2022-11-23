package com.guro.kokeetea_project.service;

import com.guro.kokeetea_project.constant.Role;
import com.guro.kokeetea_project.dto.StoreFormDTO;
import com.guro.kokeetea_project.dto.StoreInfoDTO;
import com.guro.kokeetea_project.dto.WarehouseStockInfoDTO;
import com.guro.kokeetea_project.entity.Member;
import com.guro.kokeetea_project.entity.Store;
import com.guro.kokeetea_project.entity.Warehouse;
import com.guro.kokeetea_project.repository.MemberRepository;
import com.guro.kokeetea_project.repository.RequestRepository;
import com.guro.kokeetea_project.repository.StoreRepository;
import com.guro.kokeetea_project.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final WarehouseRepository warehouseRepository;
    private final RequestRepository requestRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StoreInfoDTO> list() throws Exception {
        List<Store> stores = storeRepository.listStore();
        List<StoreInfoDTO> list = new ArrayList<>();

        for (Store store : stores){
            StoreInfoDTO dto = new StoreInfoDTO();
            dto.setId(store.getId());
            dto.setName(store.getName());
            dto.setLocation(store.getLocation());
            dto.setPhone(store.getPhone());
            dto.setEmail(store.getEmail());
            if (store.getWarehouse() != null && store.getWarehouse().getIsValid()) {
                dto.setWarehouseName(store.getWarehouse().getName());
            } else {
                dto.setWarehouseName("미배정");
            }
            list.add(dto);
        }

        return list;
    }

    @Transactional(readOnly = true)
    public StoreFormDTO original(Long id) throws Exception {
        Store store = storeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!store.getIsValid()) {
            throw new EntityNotFoundException();
        }
        StoreFormDTO dto = new StoreFormDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        dto.setPhone(store.getPhone());
        dto.setEmail(store.getEmail());
        if (store.getWarehouse()!=null && store.getWarehouse().getIsValid()) {
            dto.setWarehouseId(store.getWarehouse().getId());
        }

        return dto;
    }

    public Long create(StoreFormDTO storeFormDTO) throws Exception {
        Store store = new Store();
        store.setName(storeFormDTO.getName());
        store.setLocation(storeFormDTO.getLocation());
        store.setPhone(storeFormDTO.getPhone());
        store.setEmail(storeFormDTO.getEmail());
        store.setWarehouse(null);
        if (storeFormDTO.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(storeFormDTO.getWarehouseId()).orElse(null);
            if (warehouse!=null && warehouse.getIsValid()) {
                store.setWarehouse(warehouse);
            }
        }
        store.setIsValid(true);

        Member member = new Member();
        member.setEmail(store.getEmail());
        member.setPassword(passwordEncoder.encode("12345678"));
        member.setRole(Role.USER);
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }

        memberRepository.save(member);
        storeRepository.save(store);

        return store.getId();
    }

    public Long update(StoreFormDTO storeFormDTO) throws Exception {
        Store store = storeRepository.findById(storeFormDTO.getId()).orElseThrow(EntityNotFoundException::new);
        if (!store.getIsValid()) {
            throw new EntityNotFoundException();
        }
        store.setName(storeFormDTO.getName());
        store.setLocation(storeFormDTO.getLocation());
        store.setPhone(storeFormDTO.getPhone());
        store.setEmail(storeFormDTO.getEmail());
        store.setWarehouse(null);
        if (storeFormDTO.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(storeFormDTO.getWarehouseId()).orElse(null);
            if (warehouse!=null && warehouse.getIsValid()) {
                store.setWarehouse(warehouse);
            }
        }
        storeRepository.save(store);

        return store.getId();
    }

    public void deleteFull(Long id) throws Exception {
        Store store = storeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(store.getEmail());
        if(member != null){
            memberRepository.delete(member);
        }
        requestRepository.deleteByStore(store);
        storeRepository.deleteById(id);
    }

    public void delete(Long id) throws Exception {
        Store store = storeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(store.getEmail());
        if(member != null){
            memberRepository.delete(member);
        }
        store.setIsValid(false);
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
