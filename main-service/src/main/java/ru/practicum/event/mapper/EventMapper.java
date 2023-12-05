package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event dtoToEvent(NewEventDto newEventDto,
                                   LocalDateTime eventDate,
                                   User initiator,
                                   Category category,
                                   Location location) {
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getIsRequestModeration() == null) {
            newEventDto.setIsRequestModeration(true);
        }
        Event event = Event.builder()
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .initiator(initiator)
                .category(category)
                .location(location)
                .eventDate(eventDate)
                .build();
        event.setState(EventState.PENDING);
        return event;
    }

    public static EventFullDto eventToFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .initiator(UserMapper.userToShortDto(event.getInitiator()))
                .category(CategoryMapper.categoryToDto(event.getCategory()))
                .location(LocationMapper.locationToDto(event.getLocation()))
                .eventDate(event.getEventDate())
                .createdOn(event.getCreatedOn())
                .publishedTime(event.getPublishedTime())
                .participantLimit(event.getParticipantLimit())
                .paid(event.getPaid())
                .isRequestModeration(event.getIsRequestModeration())
                .state(event.getState())
                .build();
    }

    public static EventShortDto eventToShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .initiator(UserMapper.userToShortDto(event.getInitiator()))
                .category(CategoryMapper.categoryToDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .paid(event.getPaid())
                .build();
    }
}
