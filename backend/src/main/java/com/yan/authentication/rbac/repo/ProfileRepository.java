package com.yan.authentication.rbac.repo;

import com.yan.authentication.rbac.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByName(String name);
}


