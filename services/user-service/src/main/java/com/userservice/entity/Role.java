package com.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "roles")
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    private int id;

    @Column(name = "role_name", unique = true, nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> user;

    public Role(String roleName) {
        this.name = roleName;
    }
}
