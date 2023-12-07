package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatisticRequestDto;
import ru.practicum.dto.StatisticResponseDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.App;
import ru.practicum.storage.AppRepository;
import ru.practicum.storage.HitRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final AppRepository appRepository;
    private final HitRepository hitRepository;

    @Override
    @Transactional
    public void save(StatisticRequestDto requestDto) {
        Optional<App> checkApp = appRepository.findByName(requestDto.getApp());
        App app = new App();
        if (checkApp.isEmpty()) {
            log.info("App not found");
            app.setName(requestDto.getApp());
            appRepository.save(app);
            log.info("App saved in repository");
        } else {
            app = checkApp.get();
        }
        hitRepository.save(HitMapper.dtoToHit(requestDto, app));
        log.info("Hit saved in repository");
    }

    @Override
    @Transactional
    public List<StatisticResponseDto> getStatistic(String start, String end, Optional<List<String>> uris, boolean unique) {

        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (startTime.isAfter(endTime)) {
            throw new ValidationException("Start date is after end date");
        }
        if (unique && uris.isPresent()) {
            log.info("Hit getStatsByTimeAndUrisUnique");
            return hitRepository.getStatsByTimeAndUrisUnique(startTime, endTime, uris.get());
        }
        if (!unique && uris.isPresent()) {
            log.info("Hit getStatsByTimeAndUris");
            return hitRepository.getStatsByTimeAndUris(startTime, endTime, uris.get());
        }
        if (unique) {
            log.info("Hit getStatsByTimeUnique");
            return hitRepository.getStatsByTimeUnique(startTime, endTime);
        }
        log.info("Hit getStatsByTime");
        return hitRepository.getStatsByTime(startTime, endTime);
    }
}