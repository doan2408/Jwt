package com.example.jwt.Enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_oauth")
public class UserOauth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "provider_id", nullable = false)
    private OauthProvider provider;

    @Nationalized
    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Nationalized
    @Column(name = "email", length = 100)
    private String email;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

}