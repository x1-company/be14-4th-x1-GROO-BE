package com.x1.groo.forest.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "TempSharedForestEntity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "shared_forest")
public class SharedForestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "forest_id", nullable = false)
    private ForestEntity forest;
}
