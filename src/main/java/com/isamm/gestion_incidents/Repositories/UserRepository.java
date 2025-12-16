package com.isamm.gestion_incidents.Repositories;

import com.isamm.gestion_incidents.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional <User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);
    boolean existsByEmail(String email);
}
