package com.edgy.villains;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Villain extends PanacheEntity {

    @Column(nullable = false)
    public String name;

    public String otherName;

    public int level;

    public String picture;

    @Column(columnDefinition = "TEXT")
    public String powers;
}
