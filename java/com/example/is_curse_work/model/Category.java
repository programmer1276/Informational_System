package com.example.is_curse_work.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "perishable_days_default")
    private Integer perishableDaysDefault;

    public Long getCategoryId() { return categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPerishableDaysDefault() { return perishableDaysDefault; }
    public void setPerishableDaysDefault(Integer perishableDaysDefault) { this.perishableDaysDefault = perishableDaysDefault; }
}

