package com.yan.authentication.rbac.repo;

import com.yan.authentication.rbac.domain.PermissionMenu;
import com.yan.authentication.rbac.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PermissionMenuRepository extends JpaRepository<PermissionMenu, UUID> {
    List<PermissionMenu> findByProfileIn(Iterable<Profile> profiles);
}


