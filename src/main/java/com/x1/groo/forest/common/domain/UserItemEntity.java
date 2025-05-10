package com.x1.groo.forest.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "TempUserItemEntity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "user_item")
public class UserItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "placed_count", nullable = false)
    private Integer placedCount;

    @ManyToOne
    @JoinColumn(name = "forest_id", nullable = false)
    private ForestEntity forest;
}
