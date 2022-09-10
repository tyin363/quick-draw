package nz.ac.auckland.se206.controllers.scenemanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import javafx.util.Callback;
import nz.ac.auckland.se206.annotations.Inject;
import nz.ac.auckland.se206.annotations.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerFactory implements Callback<Class<?>, Object> {

  private final Logger logger = LoggerFactory.getLogger(ControllerFactory.class);
  private final Map<Class<?>, Function<Class<?>, Object>> suppliers = new ConcurrentHashMap<>();
  private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

  /**
   * Creates a new instance of the controller factory and automatically adds the {@link Logger}
   * supplier.
   */
  public ControllerFactory() {
    this.registerSupplier(Logger.class, LoggerFactory::getLogger);
    this.singletons.put(ObjectMapper.class, new ObjectMapper());
  }

  /**
   * Creates a new instance of the given controller class.
   *
   * @param type The type of the controller to create
   * @return The new instance of the controller or null if there was an error
   */
  @Override
  public Object call(final Class<?> type) {
    return this.getInstance(type, Void.class);
  }

  /**
   * Registers a new instance supplier for the specified type. The supplier function takes in a
   * single parameter, the class that the type is being used within and returns an instance of the
   * type that has been created.
   *
   * @param type The type that will be created by the supplier
   * @param supplier The supplier function
   */
  public void registerSupplier(final Class<?> type, final Function<Class<?>, Object> supplier) {
    this.suppliers.put(type, supplier);
  }

  /**
   * Retrieves the instance of the given class. If the class is annotated with {@code @Singleton}
   * then it will attempt to use a previously created instance of the class. Otherwise, a new
   * instance will be created.
   *
   * @param type The type of the class to retrieve
   * @param parentType The class that the type is being used within
   * @return The instance of the class or null if there was an error
   */
  private Object getInstance(final Class<?> type, final Class<?> parentType) {
    if (this.suppliers.containsKey(type)) {
      return this.suppliers.get(type).apply(parentType);
    }

    if (type.isAnnotationPresent(Singleton.class)) {
      if (!this.singletons.containsKey(type)) {
        // Store the instance to prevent it being created multiple times
        final Object instance = this.createInstance(type);
        this.singletons.put(type, instance);
      }

      return this.singletons.get(type);
    }
    // If the type is not a singleton then create a new instance every time we need it
    return this.createInstance(type);
  }

  /**
   * Constructs an instance of the given class. It will attempt to create an instance of the class
   * using the first constructor it finds that's annotated with {@code @Inject}. If no such
   * constructors exist then it will attempt to create an instance of the class using the default
   * constructor.
   *
   * @param type The type of the class to create an instance of
   * @return The new instance of the class or null if there was an error
   */
  private Object createInstance(final Class<?> type) {
    return Arrays.stream(type.getDeclaredConstructors())
        .filter(c -> c.isAnnotationPresent(Inject.class))
        .findFirst()
        .or(
            () -> {
              // Attempt to find a constructor without any parameters
              try {
                return Optional.of(type.getDeclaredConstructor());
              } catch (final NoSuchMethodException e) {
                this.logger.error(
                    "There are no bindable constructors for {}", type.getCanonicalName());
                return Optional.empty();
              }
            })
        .map(
            c -> {
              // If there is a constructor, attempt to create an instance of it
              final Object[] args = new Object[c.getParameterCount()];
              final Parameter[] parameters = c.getParameters();

              for (int i = 0; i < c.getParameterCount(); i++) {
                final Parameter parameter = parameters[i];
                // This might recursively call this method, so this implementation doesn't handle
                // situations where the parameter requires an instance of this to be created.
                args[i] = this.getInstance(parameter.getType(), type);
              }
              // The constructor might be private, so we need to make it accessible
              c.setAccessible(true);
              try {
                final Object instance = c.newInstance(args);
                // If we successfully created an instance of it, then we can check if it has any
                // fields that need to be injected.
                this.injectFields(instance);
                return instance;
              } catch (final InstantiationException
                  | IllegalAccessException
                  | InvocationTargetException e) {
                this.logger.error("Failed to create instance of {}", type.getCanonicalName(), e);
                return null;
              }
            })
        .orElse(null);
  }

  /**
   * Checks if any of the fields in the instance are annotated with {@code @Inject} and if so then
   * attempts to automatically inject the fields using either the registered suppliers or by
   * creating instances of them using the {@link #createInstance(Class)} method.
   *
   * @param instance The instance to inject the fields of
   */
  private void injectFields(final Object instance) {
    final Class<?> instanceType = instance.getClass();

    for (final Field field : instanceType.getDeclaredFields()) {
      try {
        // Some of these fields may be private, so we need to make them accessible
        field.setAccessible(true);
        if (field.isAnnotationPresent(Inject.class)) {
          // Only inject fields that are not already set
          if (field.get(instance) == null) {
            field.set(instance, this.getInstance(field.getType(), instanceType));
          }
        }
      } catch (final IllegalAccessException | InaccessibleObjectException e) {
        this.logger.error(
            "Failed to inject field {} in type {}",
            field.getName(),
            instanceType.getCanonicalName(),
            e);
      }
    }
  }
}
