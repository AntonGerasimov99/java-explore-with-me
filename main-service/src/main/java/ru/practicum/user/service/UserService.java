package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    List<UserDto> getUsersList(List<Long> ids, int from, int size);

    void deleteUser(long id);
}