package com.ejemplo.repository;

import com.ejemplo.entities.TipoMoneda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoMonedaRepository extends JpaRepository<TipoMoneda, Long> {}
