package com.example.jwt.Enity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Nationalized
    @Column(name = "password", nullable = false)
    private String password;

    @Nationalized
    @Column(name = "email", length = 100)
    private String email;

    @ColumnDefault("1")
    @Column(name = "enabled")
    private Boolean enabled;

    @ColumnDefault("'LOCAL'")
    @Column(name = "provider", length = 20)
    private String provider;

    @ColumnDefault("getdate()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("getdate()")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserOauth> userOauths = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "user")
    private Set<Role> roles = new LinkedHashSet<>();

}