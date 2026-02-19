package com.example.asm1.repository;

import com.example.asm1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Lấy sản phẩm mới nhất trước
    List<Product> findAllByOrderByCreatedAtDesc();

    // ===== FILTER THEO TÊN + GIÁ =====
    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    """)
    List<Product> filterProduct(
            @Param("keyword") String keyword,
            @Param("maxPrice") Double maxPrice
    );
}
