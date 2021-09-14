package com.example.restapistudy.events;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter @Setter @Builder
@EqualsAndHashCode(of = "id")
@Entity @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Integer id;

    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(value = EnumType.STRING)
    private Set<AccountRole> roles;
}
