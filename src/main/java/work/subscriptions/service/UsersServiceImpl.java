package work.subscriptions.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.subscriptions.dto.CreateUserDTO;
import work.subscriptions.dto.UserDTO;
import work.subscriptions.exception.UserNotFoundException;
import work.subscriptions.exception.ValidationException;
import work.subscriptions.model.User;
import work.subscriptions.repository.UserRepository;
import work.subscriptions.util.UserMapper;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService{


    private final UserRepository userRepository;

    public UsersServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        validateUserDTO(createUserDTO);


        UserDTO userDTO= new UserDTO();
        userDTO.setUsername(createUserDTO.getUsername());
        userDTO.setEmail(createUserDTO.getEmail());

        // В задании не было, но решил не хранить в базе в открытом виде в идеале, нужны авторизация и аутентификация
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(createUserDTO.getPassword());
        userDTO.setPassword(hashedPassword);

        User newUser = UserMapper.toEntity(userDTO);

        try {
            userRepository.save(newUser);
            log.info("Пользователь с ID {} успешно сохранён", newUser.getId());
            userDTO.setId(newUser.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя в БД: {}", e.getMessage());
            throw new RuntimeException("Ошибка при сохранении пользователя в БД", e);
        }

        return userDTO;
    }

    @Override
    public UserDTO getUserById(Long id) {
        if (id == null) {
            log.error("Ошибка: переданный ID равен null");
            throw new IllegalArgumentException("Переданный ID равен null");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Пользователь ID %d не найден", id);
                    log.error(errorMessage);
                    return new UserNotFoundException(errorMessage);
                });

        return UserMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь ID %d не найден" + id));

        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(userDTO.getUsername())) {
                log.error("Пользователь {} уже существует", userDTO.getUsername());
                throw new ValidationException("Пользователь уже существует");
            }
            existingUser.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                log.error("Email {} уже существует\"", userDTO.getEmail());
                throw new ValidationException("Пользователь уже существует");
            }
            existingUser.setEmail(userDTO.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toDTO(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь ID %d не найден " + id);
        }
        userRepository.deleteById(id);
    }


    private void validateUserDTO(CreateUserDTO createUserDTO) {
        if (createUserDTO == null) {
            log.error("Ошибка: переданный объект userDTO равен null");
            throw new IllegalArgumentException("Переданный объект userDTO равен null");
        }

        if (createUserDTO.getUsername() == null || createUserDTO.getUsername().isEmpty()) {
            log.error("Ошибка: имя пользователя не передано");
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (createUserDTO.getPassword() == null || createUserDTO.getPassword().isEmpty()) {
            log.error("Ошибка: пароль не передан");
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        if (createUserDTO.getEmail() == null || createUserDTO.getEmail().isEmpty()) {
            log.error("Ошибка: email не передан");
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            log.error("Ошибка: {} уже существует", createUserDTO.getUsername());
            throw new ValidationException("USername уже существует");
        }
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            log.error("Ошибка: {} уже существует", createUserDTO.getEmail());
            throw new ValidationException("Email уже существует");
        }
    }

}
