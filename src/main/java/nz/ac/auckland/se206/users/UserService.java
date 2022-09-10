package nz.ac.auckland.se206.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.util.Config;

@Singleton
public class UserService {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Inject private Config config;

  public void saveUser(final User user) {}
}
