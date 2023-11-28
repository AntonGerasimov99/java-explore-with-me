package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exception.NotFoundElementException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.dtoToUser(userDto));
        log.info("User with id = " + user.getId() + " saved");
        return UserMapper.userToDto(user);
    }

    @Override
    @Transactional
    public List<UserDto> getUsersList(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        List<UserDto> result;
        if (ids == null) {
            log.info("List ids is empty");
            result = userRepository.findAll(pageable).stream()
                    .map(UserMapper::userToDto)
                    .collect(Collectors.toList());
        } else {
            log.info("List ids is not empty");
            result = userRepository.findByIdIn(ids, pageable).stream()
                    .map(UserMapper::userToDto)
                    .collect(Collectors.toList());
        }
        return result;
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundElementException("User with id " + id + " not found"));
        userRepository.deleteById(id);
        log.info("User with id " + id + " deleted");
    }
}