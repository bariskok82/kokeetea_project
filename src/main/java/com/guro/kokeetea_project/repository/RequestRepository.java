package com.guro.kokeetea_project.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.guro.kokeetea_project.dto.StatOfRequestByMonth;
import com.guro.kokeetea_project.dto.StatOfRequestByMonthIngredient;
import com.guro.kokeetea_project.dto.StatOfRequestByMonthStore;
import com.guro.kokeetea_project.entity.Ingredient;
import com.guro.kokeetea_project.entity.Store;
import com.guro.kokeetea_project.entity.Warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.guro.kokeetea_project.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("select r from Request r order by r.date desc")
    List<Request> listRequest();

    @Query("select r from Request r order by r.date desc")
    List<Request> listRequest(Pageable pageable);

    @Query("select r from Request r where r.store.email = :email order by r.date desc")
    List<Request> mylistRequest(@Param("email") String email, Pageable pageable);

    @Query("select count(r) from Request r ")
    Long countRequest();

    @Query("select new com.guro.kokeetea_project.dto.StatOfRequestByMonth(year(min(r.date)), month(min(r.date)), count(r))"
            +"from Request r where r.date >= :start and r.date < :end group by year(r.date)*100+month(r.date) order by year(r.date)*100+month(r.date) asc")
    List<StatOfRequestByMonth> countByMonth(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new com.guro.kokeetea_project.dto.StatOfRequestByMonthIngredient(r.ingredient.id, r.ingredient.name, r.ingredient.category.id, r.ingredient.category.name, count(r))"
            +"from Request r where r.date >= :start and r.date < :end group by r.ingredient order by count(r) desc")
    List<StatOfRequestByMonthIngredient> countByMonthIngredient(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select new com.guro.kokeetea_project.dto.StatOfRequestByMonthStore(r.store.id, r.store.name, count(r))"
            +"from Request r where r.date >= :start and r.date < :end group by r.store order by count(r) desc")
    List<StatOfRequestByMonthStore> countByMonthStore(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("select count(r) from Request r where r.store.email = :email")
    Long mycountRequest(@Param("email") String email);

    List<Request> findByIngredient(Ingredient ingredient);

    void deleteByIngredient(Ingredient ingredient);

    void deleteByStore(Store store);

    void deleteByWarehouse(Warehouse warehouse);
}
