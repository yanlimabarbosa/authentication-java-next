package com.yan.authentication.rbac.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "permissions_menu", uniqueConstraints = @UniqueConstraint(columnNames = {"profile_id","menu_id"}))
@Getter
@Setter
public class PermissionMenu {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    private Boolean acessar;
    private Boolean criar;
    private Boolean editar;
    private Boolean deletar;
    private Boolean exportar;
}


