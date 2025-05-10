package com.x1.groo.forest.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "TempDiaryEmotionEntity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "diary_emotion")
public class DiaryEmotionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @ManyToOne
    @JoinColumn(name = "diary_id", nullable = false)
    private DiaryEntity diary;

    @Column(name = "emotion", nullable = false)
    private String emotion;
}
