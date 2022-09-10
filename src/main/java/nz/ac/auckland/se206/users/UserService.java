package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.auckland.se206.annotations.Singleton;

@Singleton
public class UserService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  public void saveUser(final User user) {}
}
