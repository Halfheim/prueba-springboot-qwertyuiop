package com.ejemplo.repository;

import com.ejemplo.entities.CondicionIva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondicionIvaRepository extends JpaRepository<CondicionIva, Long> {}
