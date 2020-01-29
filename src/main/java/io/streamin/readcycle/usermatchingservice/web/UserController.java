package io.streamin.readcycle.usermatchingservice.web;

import io.streamin.readcycle.usermatchingservice.engine.MatchingService;
import io.streamin.readcycle.usermatchingservice.firebase.user.User;
import io.streamin.readcycle.usermatchingservice.firebase.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Slf4j
public class UserController {

  @Autowired
  private MatchingService matchingService;
  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @GetMapping("/match/newUser")
  private void newUser(@RequestParam(name="user", required=true) String user) {
    User _user = userRepository.findById(user).block();
    List<User> _existingUsers = userRepository.findAll().collectList().block();
    matchingService.newUserMatches(_user, _existingUsers);
  }

  @GetMapping("/match/updatedUser")
  private void updatedUser(@RequestParam(name="user", required=true) String user) {
    User _user = userRepository.findById(user).block();
    List<User> _existingUsers = userRepository.findAll().collectList().block();
    matchingService.updatedUserMatches(_user, _existingUsers);
  }

  @GetMapping("/match/deletedUser")
  private void deletedUser(@RequestParam(name="user", required=true) String user) {
    User _user = userRepository.findById(user).block();
    matchingService.deletedUserMatches(_user);
  }
}
