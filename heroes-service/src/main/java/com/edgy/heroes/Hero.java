package com.edgy.heroes;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Hero extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    public String otherName;

    public int level;

    public String picture;

    @Column(columnDefinition = "TEXT")
    public String powers;
}
