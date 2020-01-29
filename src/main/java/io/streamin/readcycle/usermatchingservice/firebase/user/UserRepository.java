package io.streamin.readcycle.usermatchingservice.firebase.user;

import io.streamin.readcycle.usermatchingservice.firebase.user.User;
import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;

public interface UserRepository extends FirestoreReactiveRepository<User> {
}
