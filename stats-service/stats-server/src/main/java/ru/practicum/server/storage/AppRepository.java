package ru.practicum.server.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.server.model.App;

import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<App, Long> {

    Optional<App> findByName(String name);
}