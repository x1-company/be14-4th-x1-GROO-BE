package com.x1.groo.forest.emotion.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="placement")
public class PlacementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="position_x")
    private BigDecimal positionX;

    @Column(name="position_y")
    private BigDecimal positionY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_item_id")
    private UserItemEntity userItem;

    public PlacementEntity(BigDecimal positionX, BigDecimal positionY,
                           UserEntity user, UserItemEntity userItem) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.user = user;
        this.userItem = userItem;
    }
}
