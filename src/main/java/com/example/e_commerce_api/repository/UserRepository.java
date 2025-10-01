package com.example.e_commerce_api.repository;

import com.example.e_commerce_api.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    UserDetails findByEmail(String email);
}
