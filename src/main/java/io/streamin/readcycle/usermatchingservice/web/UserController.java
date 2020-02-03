package io.streamin.readcycle.usermatchingservice.web;

import io.streamin.readcycle.usermatchingservice.engine.MatchingService;
import io.streamin.readcycle.usermatchingservice.firebase.libraryBook.LibraryBook;
import io.streamin.readcycle.usermatchingservice.firebase.libraryBook.LibraryBookRepository;
import io.streamin.readcycle.usermatchingservice.firebase.user.User;
import io.streamin.readcycle.usermatchingservice.firebase.user.UserRepository;
import io.streamin.readcycle.usermatchingservice.firebase.userMatch.UserMatch;
import io.streamin.readcycle.usermatchingservice.firebase.userMatch.UserMatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class UserController {

  @Autowired
  private MatchingService matchingService;
  private final UserRepository userRepository;
  private final UserMatchRepository userMatchRepository;
  private final LibraryBookRepository libraryBookRepository;

  public UserController(UserRepository userRepository, LibraryBookRepository libraryBookRepository, UserMatchRepository userMatchRepository) {
    this.userRepository = userRepository;
    this.libraryBookRepository = libraryBookRepository;
    this.userMatchRepository = userMatchRepository;
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

  @GetMapping("/match/newBook")
  private void newBook(@RequestParam(name="book", required=true) String book) {
    LibraryBook _libraryBook = libraryBookRepository.findById(book).block();
    matchingService.newBookMatches(_libraryBook);
  }

  @GetMapping("/match/accepted")
  private void accepted(@RequestParam(name="match", required=true) String match) {
    UserMatch _match = userMatchRepository.findById(match).block();
    matchingService.matchAccepted(_match);
  }

  @GetMapping("/match/rejected")
  private void rejected(@RequestParam(name="match", required=true) String match) {
    UserMatch _match = userMatchRepository.findById(match).block();
    matchingService.matchRejected(_match);
  }

  @GetMapping("/match/cancelled")
  private void cancelled(@RequestParam(name="match", required=true) String match) {
    UserMatch _match = userMatchRepository.findById(match).block();
    matchingService.matchCancelled(_match);
  }

}
