package com.webalk.webapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotNull(message = "A név nem lehet üres")
    @Size(min = 2, max = 100, message = "A névnek 2 és 100 karakter között kell lennie")
    private String name;

    @Column(name = "description") 
    @Size(max = 500, message = "A leírás nem haladhatja meg az 500 karaktert")
    private String description;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Az ár nem lehet üres")
    @Positive(message = "Az árnak nagyobbnak kell lennie, mint 0")
    private Double price;

    @Column(name = "quantity", nullable = false)
    @Min(value = 1, message = "A mennyiségnek legalább 1-nek kell lennie")
    private int quantity;

    public Product() {}

    public Product(String name, String description, Double price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
