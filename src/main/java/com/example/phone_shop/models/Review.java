package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private int phoneId;
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
