package ru.practicum.request.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EventConfirmedRequests {

    private Long eventId;

    private Long amountConfirmedRequests;
}