package io.streamin.readcycle.usermatchingservice.firebase.userMatch;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.firestore.Document;

@Data
@Document(collectionName = "userMatches")
public class UserMatch {

  @DocumentId
  private String id;
  private String userThatWillTravel;
  private String userThatWillTravelName;
  private String userThatWillTravelPhoto;
  private String userThatWillStay;
  private String userThatWillStayName;
  private String userThatWillStayPhoto;
  private double distance;
  private String book1Ref;
  private String book2Ref;
  private String isbn;
  private String name;
  private String pictureURL;

}
