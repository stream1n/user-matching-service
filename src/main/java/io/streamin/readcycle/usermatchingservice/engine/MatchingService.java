package io.streamin.readcycle.usermatchingservice.engine;

import io.streamin.readcycle.usermatchingservice.firebase.libraryBook.LibraryBook;
import io.streamin.readcycle.usermatchingservice.firebase.libraryBook.LibraryBookRepository;
import io.streamin.readcycle.usermatchingservice.firebase.user.User;
import io.streamin.readcycle.usermatchingservice.firebase.user.UserRepository;
import io.streamin.readcycle.usermatchingservice.firebase.userMatch.UserMatch;
import io.streamin.readcycle.usermatchingservice.firebase.userMatch.UserMatchRepository;
import io.streamin.readcycle.usermatchingservice.firebase.userPotentialMatch.UserPotentialMatch;
import io.streamin.readcycle.usermatchingservice.firebase.userPotentialMatch.UserPotentialMatchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class MatchingService {

  private final UserPotentialMatchRepository userPotentialMatchRepository;
  private final LibraryBookRepository libraryBookRepository;
  private final UserMatchRepository userMatchRepository;
  private final UserRepository userRepository;

  public MatchingService(UserPotentialMatchRepository userPotentialMatchRepository, LibraryBookRepository libraryBookRepository, UserMatchRepository userMatchRepository, UserRepository userRepository) {
    this.userPotentialMatchRepository = userPotentialMatchRepository;
    this.libraryBookRepository = libraryBookRepository;
    this.userMatchRepository = userMatchRepository;
    this.userRepository = userRepository;
  }

  public void newUserMatches(User _new, List<User> _existing) {
    _existing
      .stream()
      .filter(_user-> !_user.getId().equals(_new.getId()))
      .forEach(_user -> checkMatch(_new, _user));
  }

  public void deletedUserMatches(User _user) {
    List<UserPotentialMatch> _travel = userPotentialMatchRepository.findByUserThatWillTravel(_user.getId()).collectList().block();
    List<UserPotentialMatch> _stay = userPotentialMatchRepository.findByUserThatWillStay(_user.getId()).collectList().block();
    List<UserPotentialMatch> _delete = Stream.of(_travel, _stay).flatMap(x -> x.stream()).collect(Collectors.toList());
    userPotentialMatchRepository.deleteAll(_delete).block();

  }

  /* For an existing user updated, we clean and re-add */
  public void updatedUserMatches(User _updated, List<User> _existing) {
    deletedUserMatches(_updated);
    newUserMatches(_updated, _existing);
  }

  public void newBookMatches(LibraryBook _libraryBook) {

    if( _libraryBook.isWanted()) {
      userPotentialMatchRepository
        .findByUserThatWillTravel(_libraryBook.getUser())
        .collectList()
        .block()
        .stream()
        .forEach(user -> checkBookMatch(user, user.getUserThatWillStay(), _libraryBook));
    } else {
      userPotentialMatchRepository
        .findByUserThatWillStay(_libraryBook.getUser())
        .collectList()
        .block()
        .stream()
        .forEach(user -> checkBookMatch(user, user.getUserThatWillTravel(), _libraryBook));
    }

  }

  private void checkBookMatch( UserPotentialMatch user, String userId, LibraryBook _libraryBook) {

    libraryBookRepository
      .findLibraryBookByUserAndNameAndWanted(userId, _libraryBook.getName(), !_libraryBook.isWanted())
      .collectList()
      .block()
      .stream()
      .forEach(match -> saveMatch(user.getUserThatWillStay(), user.getUserThatWillTravel(), match.getId(), _libraryBook.getId(), user.getDistance(), _libraryBook.getName(), _libraryBook.getIsbn(), _libraryBook.getPictureURL()));

  }

  private void saveMatch( String userThatWillStay, String userThatWillTravel, String book1Ref, String book2Ref, double distance, String name, String isbn, String pictureURL) {
    User _userThatWillStay = userRepository.findById(userThatWillStay).block();
    User _userThatWillTravel = userRepository.findById(userThatWillTravel).block();

    UserMatch match = new UserMatch();
    match.setId(UUID.randomUUID().toString());
    match.setDistance(distance);
    match.setUserThatWillStay(userThatWillStay);
    match.setUserThatWillTravel(userThatWillTravel);
    match.setBook1Ref(book1Ref);
    match.setBook2Ref(book2Ref);
    match.setName(name);
    match.setIsbn(isbn);
    match.setPictureURL(pictureURL);
    match.setUserThatWillStayName(_userThatWillStay.getUserName());
    match.setUserThatWillStayPhoto(_userThatWillStay.getUserPhoto());
    match.setUserThatWillTravelName(_userThatWillTravel.getUserName());
    match.setUserThatWillTravelPhoto(_userThatWillTravel.getUserPhoto());

    userMatchRepository.save(match).block();
  }

  private void checkMatch(User _new, User _existing) {

    double distance = calcDistance(_new.getLatitude(), _new.getLongitude(), _existing.getLatitude(), _existing.getLongitude());

    if( distance <= _existing.getMaxDistanceUserWantsToTravel() && distance <= _new.getMaxDistanceUserWantsOthersToTravelFrom() ) {
      UserPotentialMatch match = new UserPotentialMatch(UUID.randomUUID().toString(), _existing.getId(), _new.getId(), distance);
      userPotentialMatchRepository.save(match).block();
    }

    if( distance <= _new.getMaxDistanceUserWantsToTravel() && distance <= _existing.getMaxDistanceUserWantsOthersToTravelFrom() ) {
      UserPotentialMatch match = new UserPotentialMatch(UUID.randomUUID().toString(), _new.getId(), _existing.getId(), distance);
      userPotentialMatchRepository.save(match).block();
    }

  }

  private static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
    if ((lat1 == lat2) && (lon1 == lon2)) {
      return 0;
    }
    else {
      double theta = lon1 - lon2;
      double distance = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
      distance = Math.acos(distance);
      distance = Math.toDegrees(distance);
      distance = distance * 60 * 1.1515;
      distance = distance * 1.609344;
      return (distance);
    }
  }

}
