package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.storage.EventRepository;
import ru.practicum.exception.NotFoundElementException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(new ArrayList<>());
        }
        Compilation compilation = CompilationMapper.dtoToCompilation(newCompilationDto);
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
        List<EventShortDto> eventShort = events.stream()
                .map(EventMapper::eventToShortDto)
                .collect(Collectors.toList());
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.compilationToDto(compilation, eventShort);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = findCompilation(compilationId);
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        Set<Long> eventIds = updateCompilationRequest.getEvents();
        if (eventIds != null) {
            compilation.setEvents(eventRepository.findAllByIdIn(eventIds));
        }
        List<EventShortDto> eventShort = convertEventToShort(compilation);
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.compilationToDto(compilation, eventShort);
    }

    @Override
    @Transactional
    public CompilationDto getCompilation(long compilationId) {
        Compilation compilation = findCompilation(compilationId);
        List<EventShortDto> eventShort = convertEventToShort(compilation);
        return CompilationMapper.compilationToDto(compilation, eventShort);
    }

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Compilation> compilations;
        List<EventShortDto> eventShort;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable);
        } else {
            compilations = compilationRepository.findAllByPinnedIs(pinned, pageable);
        }
        List<CompilationDto> result = new ArrayList<>();
        for (Compilation com : compilations) {
            eventShort = convertEventToShort(com);
            result.add(CompilationMapper.compilationToDto(com, eventShort));
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteCompilation(long compilationId) {
        Compilation compilation = findCompilation(compilationId);
        compilationRepository.delete(compilation);
    }

    private Compilation findCompilation(long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundElementException("Compilation with id " + compilationId + " not found"));
    }

    private List<EventShortDto> convertEventToShort(Compilation compilation) {
        return compilation.getEvents().stream()
                .map(EventMapper::eventToShortDto)
                .collect(Collectors.toList());
    }
}