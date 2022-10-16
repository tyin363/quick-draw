package nz.ac.auckland.se206.badges.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import nz.ac.auckland.se206.badges.UserBadges;

public class UserBadgesDeserializer extends StdDeserializer<UserBadges> {

  /**
   * Creates a new instance of the User Badges Deserializer with the given value type.
   *
   * @param valueType The value type to deserialize
   */
  protected UserBadgesDeserializer(final Class<?> valueType) {
    super(valueType);
  }

  /**
   * Deserializes the given bit flag into a new instance of the User Badges class.
   *
   * @param parser The parser to use
   * @param context The context to use
   * @return The deserialized user badges
   * @throws IOException If an error occurs while deserializing
   */
  @Override
  public UserBadges deserialize(final JsonParser parser, final DeserializationContext context)
      throws IOException {
    return new UserBadges(context.readValue(parser, int.class));
  }
}
