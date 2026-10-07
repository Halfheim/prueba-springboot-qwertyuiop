package com.ejemplo.repository;

import com.ejemplo.entities.FacturaVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<FacturaVenta, Long>, FacturaRepositoryCustom {
}
