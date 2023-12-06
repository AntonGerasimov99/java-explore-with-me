package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilation(long compilationId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    void deleteCompilation(long compilationId);
}