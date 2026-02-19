package com.example.asm1.controller;

import com.example.asm1.entity.Product;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.ProductRepository;
import com.example.asm1.service.ProductService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ===== LIST =====
    @GetMapping("")
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product/list";
    }

    // ===== FILTER =====
    @GetMapping("/filter")
    public String filter(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double maxPrice,
            Model model
    ) {

        model.addAttribute("products",
                productRepository.filterProduct(keyword, maxPrice));

        model.addAttribute("keyword", keyword);
        model.addAttribute("maxPrice", maxPrice);

        return "product/list";
    }

    // ===== DETAIL =====
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("product",
                productRepository.findById(id).orElse(null));
        return "product/detail";
    }

    // ===== CRUD =====
    @GetMapping("/crud")
    public String crud(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("product", new Product());
        return "product/crud";
    }

    // ===== EDIT =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product",
                productRepository.findById(id).orElse(null));
        model.addAttribute("products", productRepository.findAll());
        return "product/crud";
    }

    // ===== CREATE / UPDATE =====
    @PostMapping("/create")
    public String create(
            @ModelAttribute Product product,
            @RequestParam MultipartFile imageFile
    ) throws IOException {

        productService.save(product, imageFile);
        return "redirect:/products/crud";
    }

    // ===== DELETE =====
    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id) {

        cartItemRepository.deleteByProductId(id);

        entityManager.createNativeQuery(
                        "DELETE FROM order_details WHERE product_id = ?"
                )
                .setParameter(1, id)
                .executeUpdate();

        productRepository.deleteById(id);

        return "redirect:/products/crud";
    }
}
