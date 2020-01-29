package io.streamin.readcycle.usermatchingservice.firebase.userPotentialMatch;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

public interface UserPotentialMatchRepository extends FirestoreReactiveRepository<UserPotentialMatch> {

  Flux<UserPotentialMatch> findByUserThatWillTravel(String userThatWillTravel);
  Flux<UserPotentialMatch> findByUserThatWillStay(String userThatWillStay);
}
