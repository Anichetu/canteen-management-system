package com.canteen.repository;

import com.canteen.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Query("SELECT m FROM MenuItem m WHERE LOWER(m.name) LIKE LOWER(CONCAT(:query, '%')) AND m.available = true")
    List<MenuItem> searchByName(@Param("query") String query);

    List<MenuItem> findByAvailableTrue();

    List<MenuItem> findByCategoryIgnoreCase(String category);
}
