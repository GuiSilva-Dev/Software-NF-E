package com.Guilherme.Api_Recibos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import com.Guilherme.Api_Recibos.domain.*;


@Repository
public interface ReciboRepository extends JpaRepository<ReciboEntity, Long> {

    Optional<ReciboEntity> findByRecord(String record);

}
