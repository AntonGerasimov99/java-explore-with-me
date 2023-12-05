package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EventWithConfirmedRequestDto {

    private long eventId;

    private long confirmedRequests;
}