package com.example.tech_go_api.specification;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.data.jpa.domain.Specification;

import com.example.tech_go_api.dto.PlayerDTO;
import com.example.tech_go_api.model.Player;

import jakarta.persistence.criteria.Predicate;

public class PlayerSpecification {
	
	private PlayerSpecification() {}

    public static Specification<Player> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Player> birthDateBetween(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("birthDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("birthDate"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("birthDate"), to);
            }
            return null;
        };
    }

    public static Specification<Player> hasRg(String rg) {
        return (root, query, cb) -> {
            if (rg == null || rg.isBlank()) return null;
            return cb.equal(root.get("rg"), rg);
        };
    }

    public static Specification<Player> hasCpf(String cpf) {
        return (root, query, cb) -> {
            if (cpf == null || cpf.isBlank()) return null;
            return cb.equal(root.get("cpf"), cpf);
        };
    }

    public static Specification<Player> hasPhone(String phone) {
        return (root, query, cb) -> {
            if (phone == null || phone.isBlank()) return null;
            return cb.like(root.get("phone"), "%" + phone + "%");
        };
    }

    public static Specification<Player> hasAddress(String address) {
        return (root, query, cb) -> {
            if (address == null || address.isBlank()) return null;
            return cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%");
        };
    }

    public static Specification<Player> hasSchoolName(String schoolName) {
        return (root, query, cb) -> {
            if (schoolName == null || schoolName.isBlank()) return null;
            return cb.like(cb.lower(root.get("schoolName")), "%" + schoolName.toLowerCase() + "%");
        };
    }

    public static Specification<Player> hasGrade(String grade) {
        return (root, query, cb) -> {
            if (grade == null || grade.isBlank()) return null;
            return cb.equal(root.get("grade"), grade);
        };
    }

    public static Specification<Player> hasSchoolSchedule(String schedule) {
        return (root, query, cb) -> {
            if (schedule == null || schedule.isBlank()) return null;
            return cb.equal(root.get("schoolSchedule"), schedule);
        };
    }

    public static Specification<Player> hasInstagram(String instagram) {
        return (root, query, cb) -> {
            if (instagram == null || instagram.isBlank()) return null;
            return cb.like(cb.lower(root.get("instagram")), "%" + instagram.toLowerCase() + "%");
        };
    }

    public static Specification<Player> hasFacebook(String facebook) {
        return (root, query, cb) -> {
            if (facebook == null || facebook.isBlank()) return null;
            return cb.like(cb.lower(root.get("facebook")), "%" + facebook.toLowerCase() + "%");
        };
    }

}
