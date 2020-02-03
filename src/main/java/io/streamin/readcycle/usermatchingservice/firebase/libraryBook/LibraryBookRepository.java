package io.streamin.readcycle.usermatchingservice.firebase.libraryBook;

import org.springframework.cloud.gcp.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

public interface LibraryBookRepository extends FirestoreReactiveRepository<LibraryBook> {
  Flux<LibraryBook> findLibraryBookByUserAndNameAndWanted(String user, String name, boolean wanted);
}
