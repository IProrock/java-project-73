package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")

public class UserController {
  private Logger LOGGER = Logger.getLogger("UserControllerLog");

  @Autowired
  UserService userService;

  @Autowired
  UserRepository userRepository;

  @GetMapping(path = "")
  @ResponseStatus(HttpStatus.OK)
  public List<UserDto> listUsers() {
    List<User> listOfUsers = userRepository.findAll();

    List<UserDto> listOfUserDtos = new ArrayList<>();

    if (!listOfUsers.isEmpty()) {
      listOfUserDtos = listOfUsers.stream()
              .map(x -> userToDto(x))
              .collect(Collectors.toList());
    }

    return listOfUserDtos;
  }

  @GetMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserDto showUserById(@PathVariable Long id) throws ResponseStatusException {
    Optional<User> user = userRepository.findById(id);

    UserDto userDto = userToDto(user
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found")));

    return userDto;
  }

  @PostMapping(path = "")
  @ResponseStatus(HttpStatus.OK)
  public UserDto addNewUser(@RequestBody User newUser) {

    LOGGER.info("addNewUser method entered. Body: " + newUser.toString());
    User createdUser = userService.createUser(newUser);

    return userToDto(createdUser);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public UserDto updateExistingUser(@PathVariable Long id, @RequestBody User modifiedUser) {
    Optional<User> user = userRepository.findById(id);

    User userToUpd = user
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found"));

    User updatedUser = userService.updateUser(modifiedUser, userToUpd);

    return userToDto(updatedUser);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.OK)
  public void  deleteUserById(@PathVariable Long id) {

    if(userRepository.existsById(id)) {
      userRepository.deleteById(id);
    } else {
      throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found");
    }
  }


  private static UserDto userToDto(User user) {
    UserDto userDto = new UserDto();
    userDto.setId(user.getId());
    userDto.setEmail(user.getEmail());
    userDto.setFirstName(user.getFirstName());
    userDto.setLastName(user.getLastName());
    userDto.setCreatedAt(user.getCreatedAt());

    return userDto;
  }
}
