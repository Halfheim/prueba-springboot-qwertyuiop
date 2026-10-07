package com.ejemplo.repository;

import com.ejemplo.entities.PuntoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PuntoVentaRepository extends JpaRepository<PuntoVenta, Long> {}
