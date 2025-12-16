package com.isamm.gestion_incidents.Repositories;

import java.util.Optional;

public interface QuartierRepository {
    Optional<Object> findById(Object quartierId);
}
