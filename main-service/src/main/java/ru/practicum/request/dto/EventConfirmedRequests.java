package ru.practicum.request.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventConfirmedRequests {

    private Long eventId;

    private Long amountConfirmedRequests;
}