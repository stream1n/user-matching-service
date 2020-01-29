package io.streamin.readcycle.usermatchingservice.engine;

import io.streamin.readcycle.usermatchingservice.firebase.user.User;
import io.streamin.readcycle.usermatchingservice.firebase.user.UserRepository;
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

  private final UserPotentialMatchRepository _repository;

  public MatchingService(UserPotentialMatchRepository _repository) {
    this._repository = _repository;
  }

  public void newUserMatches(User _new, List<User> _existing) {
    _existing
      .stream()
      .filter(_user-> !_user.getId().equals(_new.getId()))
      .forEach(_user -> checkMatch(_new, _user));
  }

  public void deletedUserMatches(User _user) {
    List<UserPotentialMatch> _travel = _repository.findByUserThatWillTravel(_user.getId()).collectList().block();
    List<UserPotentialMatch> _stay = _repository.findByUserThatWillStay(_user.getId()).collectList().block();
    List<UserPotentialMatch> _delete = Stream.of(_travel, _stay).flatMap(x -> x.stream()).collect(Collectors.toList());
    _repository.deleteAll(_delete).block();

  }

  /* For an existing user updated, we clean and re-add */
  public void updatedUserMatches(User _updated, List<User> _existing) {
    deletedUserMatches(_updated);
    newUserMatches(_updated, _existing);
  }

  private void checkMatch(User _new, User _existing) {

    double distance = calcDistance(_new.getLatitude(), _new.getLongitude(), _existing.getLatitude(), _existing.getLongitude());

    if( distance <= _existing.getMaxDistanceUserWantsToTravel() && distance <= _new.getMaxDistanceUserWantsOthersToTravelFrom() ) {
      UserPotentialMatch match = new UserPotentialMatch(UUID.randomUUID().toString(), _existing.getId(), _new.getId(), distance);
      _repository.save(match).block();
    }

    if( distance <= _new.getMaxDistanceUserWantsToTravel() && distance <= _existing.getMaxDistanceUserWantsOthersToTravelFrom() ) {
      UserPotentialMatch match = new UserPotentialMatch(UUID.randomUUID().toString(), _new.getId(), _existing.getId(), distance);
      _repository.save(match).block();
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
