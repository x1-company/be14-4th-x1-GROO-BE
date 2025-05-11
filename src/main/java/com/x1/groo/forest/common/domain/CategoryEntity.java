package com.x1.groo.forest.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "TempCategoryEntity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "category", nullable = false)
    private String category;
}
