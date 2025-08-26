package org.example.blogbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.blogbackend.model.entities.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

}
