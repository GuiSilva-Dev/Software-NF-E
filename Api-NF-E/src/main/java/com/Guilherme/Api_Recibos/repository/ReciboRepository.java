package com.Guilherme.Api_Recibos.repository;

import com.Guilherme.Api_Recibos.domain.ReciboEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReciboRepository extends MongoRepository<ReciboEntity, String> {
    Optional<ReciboEntity> findByRecord(String record);
}
