package com.example.tech_go_api.repositories;

import com.example.tech_go_api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
