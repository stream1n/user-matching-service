package io.streamin.readcycle.usermatchingservice.firebase.userPotentialMatch;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.gcp.data.firestore.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collectionName = "userPotentialMatches")
public class UserPotentialMatch {

  @DocumentId
  private String id;
  private String userThatWillTravel;
  private String userThatWillStay;
  private double distance;

}
