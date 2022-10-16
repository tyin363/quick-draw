package nz.ac.auckland.se206.badges.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import nz.ac.auckland.se206.badges.UserBadges;

public class UserBadgesSerializer extends StdSerializer<UserBadges> {

  /**
   * Creates a new instance of the User Badges Serializer.
   *
   * @param type The type to serialize
   */
  protected UserBadgesSerializer(final Class<UserBadges> type) {
    super(type);
  }

  /**
   * Serialize the user badge object as just the bit flag.
   *
   * @param value The user badges to serialize
   * @param gen The generator to use
   * @param provider The provider to use
   * @throws IOException If an error occurs while serializing
   */
  @Override
  public void serialize(
      final UserBadges value, final JsonGenerator gen, final SerializerProvider provider)
      throws IOException {
    provider.defaultSerializeValue(value.getBitFlag(), gen);
  }
}
