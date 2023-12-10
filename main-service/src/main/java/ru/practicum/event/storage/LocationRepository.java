package ru.practicum.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLon(float lat, float lon);
}