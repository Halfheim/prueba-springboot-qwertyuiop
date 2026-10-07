package com.ejemplo.repository;

import com.ejemplo.entities.ListaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Long> {}
