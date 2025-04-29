package com.x1.groo.forest.emotion.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name="ForestUserEntity")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name="user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="email")
    private String email;

    @Column(name="password")
    private String password;

    @Column(name="nickname")
    private String nickname;

    @Column(name="oauth_provider")
    private String oauthProvider;

    @Column(name="oauth_id")
    private String oauthId;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="role")
    private String role;

    @Column(name="birth")
    private LocalDateTime birth;

    @Column(name="is_deleted")
    private boolean isDeleted;
}
