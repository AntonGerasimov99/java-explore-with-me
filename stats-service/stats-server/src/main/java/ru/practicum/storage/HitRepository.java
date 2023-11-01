package ru.practicum.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatisticResponseDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.dto.StatisticResponseDto(a.name, h.uri, count(h.ip))" +
            "from Hit as h join App as a on h.app.id = a.id " +
            "where h.timestamp between :start and :end " +
            "and h.uri in (:uris) " +
            "group by a.name, h.uri " +
            "order by count(h.ip) desc")
    List<StatisticResponseDto> getStatsByTimeAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatisticResponseDto(a.name, h.uri, count(h.ip))" +
            "from Hit as h join App as a on h.app.id = a.id " +
            "where h.timestamp between :start and :end " +
            "and h.uri in (:uris) " +
            "group by a.name, h.uri " +
            "order by count(h.ip) desc")
    List<StatisticResponseDto> getStatsByTimeAndUrisUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatisticResponseDto(a.name, h.uri, count(h.ip))" +
            "from Hit as h join App as a on h.app.id = a.id " +
            "where h.timestamp between :start and :end " +
            "group by a.name, h.uri " +
            "order by count(h.ip) desc")
    List<StatisticResponseDto> getStatsByTime(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.dto.StatisticResponseDto(a.name, h.uri, count(h.ip))" +
            "from Hit as h join App as a on h.app.id = a.id " +
            "where h.timestamp between :start and :end " +
            "group by a.name, h.uri " +
            "order by count(h.ip) desc")
    List<StatisticResponseDto> getStatsByTimeUnique(LocalDateTime start, LocalDateTime end);
}