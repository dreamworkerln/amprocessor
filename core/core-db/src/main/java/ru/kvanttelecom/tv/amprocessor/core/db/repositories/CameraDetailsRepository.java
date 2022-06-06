package ru.kvanttelecom.tv.amprocessor.core.db.repositories;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.dreamworkerln.spring.db.repositories.CustomRepository;
import ru.kvanttelecom.tv.amprocessor.core.db.entities.CameraDetailsEntity;

import java.util.Optional;


public interface CameraDetailsRepository extends CustomRepository<CameraDetailsEntity, Long> {

    Optional<CameraDetailsEntity> findByName(String name);

    @Query("SELECT COUNT(cd) FROM CameraDetailsEntity cd")
    long count();

    void deleteByName(String name);
}

