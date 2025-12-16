package com.isamm.gestion_incidents.Repositories;

import com.isamm.gestion_incidents.Models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}
