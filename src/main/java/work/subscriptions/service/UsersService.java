package work.subscriptions.service;

import work.subscriptions.dto.CreateUserDTO;
import work.subscriptions.dto.UserDTO;

import java.util.UUID;

public interface UsersService {
    UserDTO createUser(CreateUserDTO userDTO);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);
}
