package com.pcbuilder.ms_login.repository;

import com.pcbuilder.ms_login.entity.HistorialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<HistorialLogin, Long> {
}