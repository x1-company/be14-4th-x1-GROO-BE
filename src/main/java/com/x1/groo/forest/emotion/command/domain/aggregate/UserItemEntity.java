package com.x1.groo.forest.emotion.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="user_item")
public class UserItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="item_id")
    private int itemId;

    @Column(name="user_id")
    private int userId;

    @Column(name="total_count")
    private int totalCount;

    @Column(name="placed_count")
    private int placedCount;

    // placed_count를 -1 하는 로직
    public void decreasePlacedCount() {
        if (this.placedCount <= 0) {
            throw new IllegalStateException("배치된 아이템이 없습니다.");
        }
        this.placedCount--;
    }
}
