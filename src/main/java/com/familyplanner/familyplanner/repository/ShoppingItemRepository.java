package com.familyplanner.familyplanner.repository;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.ShoppingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingItemRepository extends JpaRepository<ShoppingItem, Long> {
    List<ShoppingItem> findByFamilyOrderByBoughtAscCategoryAsc(Family family);
    List<ShoppingItem> findByFamilyAndBoughtTrue(Family family);
}
