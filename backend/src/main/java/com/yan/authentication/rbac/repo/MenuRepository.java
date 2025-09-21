package com.yan.authentication.rbac.repo;

import com.yan.authentication.rbac.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<Menu, UUID> {
    Optional<Menu> findByKey(String key);
}


