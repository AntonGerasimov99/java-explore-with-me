package ru.practicum.event.mapper;

import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.comment.dto.CommentFullDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

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
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
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
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        } else {
            event.setPaid(newEventDto.getPaid());
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setIsRequestModeration(false);
        } else {
            event.setIsRequestModeration(newEventDto.getRequestModeration());
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        } else {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        event.setState(EventState.PENDING);
        return event;
    }

    public static EventFullDto eventToFullDto(Event event, List<CommentFullDto> comments) {
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
                .publishedOn(event.getPublishedTime())
                .participantLimit(event.getParticipantLimit())
                .paid(event.getPaid())
                .requestModeration(event.getIsRequestModeration())
                .state(event.getState())
                .comments(comments)
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
