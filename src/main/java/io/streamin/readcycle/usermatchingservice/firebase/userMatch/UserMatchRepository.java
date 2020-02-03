package io.streamin.readcycle.usermatchingservice.firebase.userMatch;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

public interface UserMatchRepository extends FirestoreReactiveRepository<UserMatch> {
}
