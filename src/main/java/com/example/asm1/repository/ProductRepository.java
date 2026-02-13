package com.example.asm1.repository;

import com.example.asm1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // LẤY SẢN PHẨM MỚI NHẤT TRƯỚC
    List<Product> findAllByOrderByCreatedAtDesc();

}
