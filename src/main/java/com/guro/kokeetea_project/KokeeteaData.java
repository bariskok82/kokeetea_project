package com.guro.kokeetea_project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.guro.kokeetea_project.constant.RequestStatus;
import com.guro.kokeetea_project.constant.Role;
import com.guro.kokeetea_project.entity.Category;
import com.guro.kokeetea_project.entity.CurrentStock;
import com.guro.kokeetea_project.entity.Ingredient;
import com.guro.kokeetea_project.entity.Member;
import com.guro.kokeetea_project.entity.Request;
import com.guro.kokeetea_project.entity.Store;
import com.guro.kokeetea_project.entity.Warehouse;
import com.guro.kokeetea_project.repository.CategoryRepository;
import com.guro.kokeetea_project.repository.CurrentStockRepository;
import com.guro.kokeetea_project.repository.IngredientRepository;
import com.guro.kokeetea_project.repository.MemberRepository;
import com.guro.kokeetea_project.repository.RequestRepository;
import com.guro.kokeetea_project.repository.StoreRepository;
import com.guro.kokeetea_project.repository.WarehouseRepository;

@SpringBootApplication
@Profile("sample")
public class KokeeteaData implements CommandLineRunner {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private CurrentStockRepository currentStockRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(KokeeteaData.class);
        springApplication.setAdditionalProfiles("sample");
        springApplication.run(args);
    }

    @Override
    public void run(String... strings) throws Exception {
        requestRepository.deleteAll();
        memberRepository.deleteAll();
        storeRepository.deleteAll();
        currentStockRepository.deleteAll();
        warehouseRepository.deleteAll();
        ingredientRepository.deleteAll();
        categoryRepository.deleteAll();

        List<Category> categoryList = new ArrayList<>();
        for (int i=0; i<4; i++) {
            Category category = new Category();
            category.setName(List.of("커피원두", "차잎", "과일즙", "부재료").get(i));
            category.setIsValid(true);
            categoryList.add(category);
        }
        categoryRepository.saveAll(categoryList);

        List<Ingredient> ingredientList = new ArrayList<>();
        for (int i=0; i<12; i++) {
            Ingredient ingredient = new Ingredient();
            ingredient.setName(List.of("에티오피아 커피원두", "콜롬비아 커피원두", "브라질 커피원두",
                                "홍차잎", "녹차잎", "사과즙", "오렌지즙", "포도즙", "레몬즙",
                                "우유", "꿀", "얼음", "시나몬", "민트", "타피오카 펄").get(i));
            ingredient.setCategory(categoryList.get(List.of(0, 0, 0, 1, 1, 2, 2, 2, 2,
                                3, 3, 3, 3, 3, 3).get(i)));
            ingredient.setPrice((long) Math.floor(Math.random()*11)*50L+1000L);
            ingredient.setIsValid(true);
            ingredientList.add(ingredient);
        }
        ingredientRepository.saveAll(ingredientList);

        List<Warehouse> warehouseList = new ArrayList<>();
        List<CurrentStock> currentStockList = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Warehouse warehouse = new Warehouse();
            warehouse.setName(List.of("보스턴1창고", "뉴욕1창고", "시카고1창고").get(i));
            warehouse.setLocation(List.of("보스턴", "뉴욕", "시카고").get(i));
            warehouse.setEmail(List.of("boss", "choo", "polk").get(i)
                                +String.valueOf((long) Math.floor(500+Math.random()*500))+"@gmail.com");
            warehouse.setPhone("010"+String.valueOf((long) Math.floor(1000+Math.random()*9000))+String.valueOf(3200+i*9));
            warehouse.setIsValid(true);
            for (Ingredient ingredient : ingredientRepository.listIngredient()) {
                CurrentStock currentStock = new CurrentStock();
                currentStock.setIngredient(ingredient);
                currentStock.setWarehouse(warehouse);
                currentStock.setAmount((long) Math.floor(100+Math.random()*900));
                currentStockList.add(currentStock);
            }
            warehouseList.add(warehouse);
        }
        warehouseRepository.saveAll(warehouseList);
        currentStockRepository.saveAll(currentStockList);

        List<Store> storeList = new ArrayList<>();
        List<Member> memberList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            Store store = new Store();
            store.setName(List.of("메인 1호점", "메인 2호점", "플라자 1호점",
                            "싱클레어 1호점", "싱클레어 2호점", "브루클린 1호점", "브루클린 2호점",
                            "레베카 1호점", "업타운 1호점", "업타운 2호점").get(i));
            store.setWarehouse(warehouseList.get(List.of(0, 0, 0, 1, 1, 1, 1, 2, 2, 2).get(i)));
            store.setLocation(store.getWarehouse().getLocation());
            store.setEmail(List.of("kray", "peon", "cem", "walru", "java",
                            "tutu", "muni", "ban", "vox", "chiwa").get(i)
                            +String.valueOf((long) Math.floor(500+Math.random()*500))+"@gmail.com");
            store.setPhone("010"+String.valueOf((long) Math.floor(1000+Math.random()*9000))+String.valueOf(1500+i*7));
            store.setIsValid(true);
            storeList.add(store);

            Member member = new Member();
            member.setEmail(store.getEmail());
            member.setPassword(passwordEncoder.encode("12345678"));
            member.setRole(Role.USER);
            memberList.add(member);
        }
        storeRepository.saveAll(storeList);
        memberRepository.saveAll(memberList);

        Member member = new Member();
        member.setEmail("kokeetea@gmail.com");
        member.setPassword(passwordEncoder.encode("12345678"));
        member.setRole(Role.ADMIN);
        memberRepository.save(member);

        List<Request> requestList = new ArrayList<>();
        for (int i=0; i<4000; i++) {
            Request request = new Request();
            request.setDate(LocalDateTime.now().minusMonths((long) Math.floor(0+Math.random()*18)));
            request.setIngredient(ingredientRepository.listIngredient().get((int) Math.floor(Math.random()*ingredientRepository.countIngredient())));
            request.setAmount((long) Math.floor(1+Math.random()*12));
            request.setStore(storeRepository.listStore().get((int) Math.floor(Math.random()*storeRepository.countStore())));
            request.setWarehouse(request.getStore().getWarehouse());
            request.setStatus(RequestStatus.PENDING);
            requestList.add(request);
        }
        requestRepository.saveAll(requestList);
    }
}
