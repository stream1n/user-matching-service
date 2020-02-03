package io.streamin.readcycle.usermatchingservice.firebase.libraryBook;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.Data;
import org.springframework.cloud.gcp.data.firestore.Document;

@Data
@Document(collectionName = "libraryBooks")
public class LibraryBook {

  @DocumentId
  private String id;

  private String isbn;
  private String name;
  private String pictureURL;
  private String user;
  private boolean wanted;

}
