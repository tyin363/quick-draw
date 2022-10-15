# SE206 - 2022 - Beta & Final Releases

## Summary

- [Dependency Injection](#dependency-injection)
  - [Singletons](#singletons)
- [Event Listeners](#event-listeners)
  - [Ordering of Lifecycle](#ordering-of-lifecycle)
- [Switching Views](#switching-views)
- [Putting it all Together](#putting-it-all-together)

## Dependency Injection

Dependency injection is the process of providing a class with instances of other classes it
needs to function (I.e. Dependencies).

There are 2 ways to inject dependencies into a class:
1. Through the constructor. This can be done by annotating a constructor with `@Inject`. When
   an instance of the class is created all the parameters in the constructor will automatically be 
   passed in.
2. Through fields. This can be done by annotating a field with `@Inject`. When an instance of
   the class is created all the fields annotated with `@Inject` will be set. As the fields can't
   be modified until after an instance of the class is created, the fields will not be injected
   if you try and access them from the constructor. This is actually how annotating JavaFX
   fields with `@FXML` works too!

> **Warning**
> 
> Currently, dependency injection doesn't support circular dependencies. I.e. if you had 2
> classes, `A` and `B`:
> ```java
> public class A {
>   @Inject
>   public A(final B b) {
>     // Try and inject B into A
>   }
> }
>
> //...
>
> public class B {
>   @Inject
>   public B(final A a) {
>     // Try and inject A into B
>   }
> }
> ```
> When it tries to create an instance of `A` it will see that it needs an instance of `B`. It 
> will attempt to create an instance of it to use, however, to create `B` it requires an 
> instance of `A`, which it will also try and create and so the cycle continues. This infinite loop 
> will eventually cause a stack overflow error.

### Singletons

By default, whenever we need to inject a dependency into a class, it will create a completely
new instance each time. However, generally we want to reuse the same instance of a class 
whenever we inject it. This is particularly important if we're creating classes that are 
dependencies for multiple different classes, and we want each of these classes to be referencing 
the same instance. This is where the singleton design pattern comes in. It ensures that only 
one instance of a class will be created and that the instance will be reused whenever else we 
need to access it. This design pattern is supported by dependency injection by annotating the 
class we want to be a singleton with `@Singleton`. Whenever a singleton dependency is created, 
it first checks if there's already a stored instance. If there is, it'll be reused, otherwise a 
new instance is created and stored for future use.

E.g:

```java
// Mark ServiceA as a singleton
@Singleton
public class ServiceA {
}
```

## Event Listeners

There are 3 interfaces that different objects can implement to respond to certain events.

1. `LoadListener` - **Controllers** can implement this interface. It requires you to create
   an `onLoad` method which will be called **everytime** the view associated with that controller is switched to.
   This is the method where you should initialise/reset UI which can be changed while the user was previously on 
   the same view. For example with the canvas view, after you complete a drawing, a button that 
   allows you to start a new game is displayed. When we switch back to the canvas view again, 
   in this method we can reset it to make this button invisible.

   > **Note**
   > 
   > The `initialize` method is only called once when the controller is first created and should be
   > used for one-off initialisations, such as creating instances of classes that are used by the
   > controller.

2. `EnableListener` - **Anything being injected** can implement this interface.
   It requires you to create an `onEnable` method which is primarily designed for non-controller
   classes which need to initialise themselves just after all their dependencies have been injected.
   (As they don't have access to the `onLoad` and `initialize` methods).

3. `TerminationListener` - **Any singleton** can implement this interface. It requires you to create an 
   `onTerminate` method that is called just before the application is closed. This is where any termination
    of processes or saving of data should be handled.

### Ordering of Lifecycle

1. Constructor dependencies are injected
2. Constructor is called
3. Field dependencies are injected
4. `onEnable` is called (Only occurs once)
5. `initialize` is called (Only occurs once)
6. `onLoad` is called (This happens whenever the view is switched to)
7. `onTerminate` is called (This happens just before the application is closed)

## Switching Views

Switching between views is very easy. Each JavaFX view should have an associated value in the 
`View` enum. By default, the `.fxml` file name for the view is the same as the name of the enum 
but in lowercase. If it is different, you can specify it like: 
`EXAMPLE_VIEW("i_have_a_different_name")`. We can then switch between views whenever we need by
injecting `SceneManager` and passing the view to the `switchToView` method. E.g:

```java
@Inject private SceneManager sceneManager;

//...

// Change to the Canvas view
this.sceneManager.switchToView(View.CANVAS);
```

## Putting it all Together

Let's consider an example where we want to create a `WordService` that manages the current word 
being drawn. We want this class to be a singleton so that we can access the same instance of it 
from two different controllers. One controller will be responsible for generating a new random 
word everytime it's switched to and the other will need to be able to read the word and display it 
in a label.

```java
import java.awt.Label;
import javafx.fxml.FXML;
import nz.ac.auckland.se206.annotations.Singleton;
import nz.ac.auckland.se206.controllers.scenemanager.listeners.LoadListener;

@Singleton
public class WordService {

  private String targetWord;

  public void generateRandomWord() {
    // Generate a random word and store it in targetWord
  }

  public String getTargetWord() {
    return this.targetWord;
  }

}

@Singleton
public class ControllerA implements LoadListener {

  @Inject private WordService wordService;

  @Override
  public void onLoad() {
    // Generate a random word everytime the view associated with this controller is switched to
    this.wordService.generateRandomWord();
  }
}

@Singleton
public class ControllerB implements LoadListener {

  @FXML private Label wordLabel;
  @Inject private WordService wordService;

  @Override
  public void onLoad() {
    // Display the current word everytime the view associated with this controller is switched to
    this.wordLabel.setText(this.wordService.getTargetWord());
  }
}
```

### Quick, Draw!

**Requirements**

- Java JDK 17.0.2 (download
  it [https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) )
  and JAVA_HOME env variable properly set
- Scene Builder (download it
  here [https://gluonhq.com/products/scene-builder/#download](https://gluonhq.com/products/scene-builder/#download))

**What to do first?**

Make sure that the provided tests pass.

Unix/MacOsX:  
`./mvnw clean test`

Windows:  
`.\mvnw.cmd clean test`

This will also install the GIT pre-commit hooks to activate the auto-formatting at every GIT commit.

**How to run the game?**

Unix/MacOsX:  
`./mvnw clean javafx:run`

Windows:  
`.\mvnw.cmd clean javafx:run`

**How to format the Java code?**

You can format the code at any time by running the command:

Unix/MacOsX:  
`./mvnw git-code-format:format-code `

Windows:  
`.\mvnw.cmd git-code-format:format-code `

## Attributions
External resources used:  
- "[fire - solid](https://fontawesome.com/icons/fire?f=classic&s=solid)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[chevron-left - solid](https://fontawesome.com/icons/chevron-left?s=solid&f=classic)" by [Font 
  Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[chevron-right - solid](https://fontawesome.com/icons/chevron-right?s=solid&f=classic)" by [Font 
  Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[eraser - solid](https://fontawesome.com/icons/eraser?s=solid&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[Clock - regular](https://fontawesome.com/icons/clock?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[Fire flame curved - solid](https://fontawesome.com/icons/fire-flame-curved?s=solid&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[plus - solid](https://fontawesome.com/icons/plus?s=solid&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[Pen - solid](https://fontawesome.com/icons/pen?s=solid&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[User - regular](https://fontawesome.com/icons/user?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://creativecommons.org/licenses/by/4.0/). Has been resized and recoloured.
- "[Circle-check - regular](https://fontawesome.com/icons/user?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://fontawesome.com/icons/circle-check?s=regular&f=classic). Has been resized and recoloured.
- "[Fire-flame-simple - regular](https://fontawesome.com/icons/user?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://fontawesome.com/icons/fire-flame-simple?s=solid&f=classic). Has been resized and recoloured.
- "[Eye-slash - solid](https://fontawesome.com/icons/user?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://fontawesome.com/icons/eye-slash?s=solid&f=classic). Has been resized and recoloured.
- "[Eye - solid](https://fontawesome.com/icons/user?s=regular&f=classic)" by [Font Awesome](https://fontawesome.com) is licensed under [CC BY 4.0 license](https://fontawesome.com/icons/eye?s=solid&f=classic). Has been resized and recoloured.