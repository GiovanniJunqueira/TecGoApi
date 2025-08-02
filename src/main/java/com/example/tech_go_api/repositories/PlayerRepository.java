package com.example.tech_go_api.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.tech_go_api.model.Player;

public interface PlayerRepository extends JpaRepository<Player, UUID>, JpaSpecificationExecutor<Player>{

}
