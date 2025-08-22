package com.projeto.vendas.domain.repositories;

import com.projeto.vendas.domain.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    Optional<Venda> findByNumeroVenda(String numeroVenda);
    boolean existsByNumeroVenda(String numeroVenda);
}