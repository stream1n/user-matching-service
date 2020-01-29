package io.streamin.readcycle.usermatchingservice.firebase.user;

import lombok.Data;
import org.springframework.cloud.gcp.data.firestore.Document;

@Data
@Document(collectionName = "userLocations")
public class User {

  private String id;
  private String name;
  private String isoCountryCode;
  private String country;
  private String postalCode;
  private String locality;
  private double longitude;
  private double latitude;
  private double maxDistanceUserWantsToTravel;
  private double maxDistanceUserWantsOthersToTravelFrom;

}
