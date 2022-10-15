package nz.ac.auckland.se206.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {

  /**
   * Whether the super class should be checked for injectable fields.
   *
   * @return If the super class should be checked for injectable fields
   */
  boolean injectSuper() default false;
}
