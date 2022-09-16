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
import nz.ac.auckland.se206.controllers.scenemanager.listeners.EnableListener;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.TerminationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstanceFactory implements Callback<Class<?>, Object>, TerminationListener {

  private final Logger logger = LoggerFactory.getLogger(InstanceFactory.class);
  private final Map<Class<?>, Function<Class<?>, Object>> suppliers = new ConcurrentHashMap<>();
  private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();

  /**
   * Creates a new instance of the controller factory and automatically adds the {@link Logger} and
   * {@link ObjectMapper} suppliers.
   */
  public InstanceFactory() {
    this.registerSupplier(Logger.class, LoggerFactory::getLogger);
    this.bind(new ObjectMapper());
  }

  /**
   * Binds the given object as a singleton of it's class, so that any time it's class is requested,
   * this instance will be returned. If there already exists an instance bound to the given class,
   * it will be replaced.
   *
   * @param instance The singleton instance
   * @param <T> The type of the instance
   * @see #bind(Class, Object)
   */
  @SuppressWarnings("unchecked")
  public <T> void bind(final T instance) {
    this.bind((Class<T>) instance.getClass(), instance);
  }

  /**
   * Binds the given object as a singleton of the given class, so that any time the given class is
   * requested, this instance will be returned. If there already exists an instance bound to the
   * given class, it will be replaced.
   *
   * @param type The class to bind the instance to
   * @param instance The singleton instance
   * @param <T> The type of the instance
   */
  public <T> void bind(final Class<T> type, final T instance) {
    this.singletons.put(type, instance);
  }

  /**
   * Retrieves the instance associated with this type. If this type is not a singleton, then a new
   * instance will be created everytime. If there is an error trying to create the instance, then
   * null will be returned.
   *
   * @param type The type of the instance to retrieve
   * @param <T> The type of the instance
   * @return The instance associated with this type, or null if there is an error creating the
   *     instance
   * @see #getSafely(Class)
   */
  @SuppressWarnings("unchecked")
  public <T> T get(final Class<T> type) {
    return (T) this.getInstance(type, Void.class);
  }

  /**
   * Safely retrieve the instance associated with this type by wrapping the result of {@link
   * #get(Class)} in an {@code Optional}.
   *
   * @param type The type of the instance to retrieve
   * @param <T> The type of the instance
   * @return An {@code Optional} containing the instance associated with this type.
   * @see #get(Class)
   */
  public <T> Optional<T> getSafely(final Class<T> type) {
    return Optional.ofNullable(this.get(type));
  }

  /**
   * Creates a new instance of the given controller class.
   *
   * @param type The type of the controller to create
   * @return The new instance of the controller or null if there was an error
   * @see #get(Class)
   */
  @Override
  public Object call(final Class<?> type) {
    return this.get(type);
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
    if (this.singletons.containsKey(type)) {
      return this.singletons.get(type);
    }

    // There is no instance of this type currently, so we'll need to make a new one
    final Object instance = this.createInstance(type);
    if (type.isAnnotationPresent(Singleton.class)) {
      // Store the instance to prevent it being created multiple times
      this.singletons.put(type, instance);
    }
    return instance;
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
                // situations where the parameter requires an instance of this to be created
                args[i] = this.getInstance(parameter.getType(), type);
              }
              // The constructor might be private, so we need to make it accessible
              c.setAccessible(true);
              try {
                final Object instance = c.newInstance(args);
                // If we successfully created an instance of it, then we can check if it has any
                // fields that need to be injected.
                this.injectFields(instance);

                // After all the fields have been injected see if it listens to onEnable
                if (instance instanceof EnableListener enableListener) {
                  enableListener.onEnable();
                }
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

  /**
   * Iterates through all the cached singletons and if they're an instance of {@link
   * TerminationListener} then it invokes the {@link TerminationListener#onTerminate()} method.
   */
  @Override
  public void onTerminate() {
    this.singletons
        .values()
        .forEach(
            instance -> {
              if (instance instanceof TerminationListener terminationListener) {
                terminationListener.onTerminate();
              }
            });
  }
}
