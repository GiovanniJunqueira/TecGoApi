package com.example.tech_go_api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.example.tech_go_api.domain.staff.Permission;
import com.example.tech_go_api.domain.staff.StaffMember;

class RedisCacheConfigTest {

    @Test
    void staffComDataDeAdmissaoPassaPeloCache() {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(RedisCacheConfig.cacheObjectMapper());

        StaffMember staff = new StaffMember();
        staff.setId("abc");
        staff.setEmail("prof@example.com");
        staff.setFirstname("Prof");
        staff.setAdmissionDate(LocalDate.of(2026, 9, 1));
        staff.setSalary(new BigDecimal("1500.00"));
        staff.setPermissions(new HashSet<>(Set.of(Permission.ATLETAS_VER)));

        Object restored = serializer.deserialize(serializer.serialize(staff));

        StaffMember copy = assertInstanceOf(StaffMember.class, restored);
        assertEquals(LocalDate.of(2026, 9, 1), copy.getAdmissionDate());
        assertEquals(new BigDecimal("1500.00"), copy.getSalary());
        assertEquals(Set.of(Permission.ATLETAS_VER), copy.getPermissions());
    }
}
