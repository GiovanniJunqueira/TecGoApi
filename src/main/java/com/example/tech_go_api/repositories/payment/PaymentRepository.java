package com.example.tech_go_api.repositories.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tech_go_api.domain.payment.Payment;
import com.example.tech_go_api.domain.school.School;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByProfilePlayerId(String playerId);
    List<Payment> findByMonth(String month);

    boolean existsByProfilePlayerIdAndMonth(String playerId, String month);
    List<Payment> findByStatus(boolean status);

    @Query("SELECT DISTINCT p FROM Payment p WHERE p.profilePlayer.school = :school "
            + "AND (:month IS NULL OR :month = '' OR p.month = :month) "
            + "AND (:status IS NULL OR p.status = :status) "
            + "AND (:search IS NULL OR :search = '' "
            + "     OR LOWER(p.profilePlayer.firstname) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR LOWER(p.profilePlayer.lastname) LIKE LOWER(CONCAT('%', :search, '%')) "
            + "     OR EXISTS (SELECT 1 FROM Responsible r JOIN r.players rp "
            + "                WHERE rp = p.profilePlayer AND LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))))")
    List<Payment> search(
            @Param("school") School school,
            @Param("month") String month,
            @Param("status") Boolean status,
            @Param("search") String search);
}

