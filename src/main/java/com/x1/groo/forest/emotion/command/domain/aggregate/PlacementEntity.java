package com.x1.groo.forest.emotion.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

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
    private int positionX;

    @Column(name="position_y")
    private int positionY;

    @Column(name="forest_id")
    private int forestId;

    @Column(name="user_id")
    private int userId;

    @Column(name="user_item_id")
    private int userItemId;
}
