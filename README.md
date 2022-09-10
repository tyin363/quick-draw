# SE206 - 2022 - Beta & Final Releases

## Summary

- [Dependency Injection](#dependency-injection)
  - [Singletons](#singletons)
- [Controller Lifecycle](#controller-lifecycle)
  - [Ordering of Lifecycle](#ordering-of-lifecycle)
- [Switching Views](#switching-views)
- [Putting it all Together](#putting-it-all-together)

## Dependency Injection

Dependency injection is the process of providing a class with instances of other classes it
needs to function (I.e. Dependencies).

There are 2 ways to inject dependencies into a class:
1. Through the constructor. This can be done by annotating a constructor with `@Inject`. When
   an instance of the class is created all the parameters in the constructor will be passed in.
2. Through fields. This can be done by annotating a field with `@Inject`. When an instance of
   the class is created all the fields annotated with `@Inject` will be set. As the fields can't
   be modified until after an instance of the class is created, the fields will not be injected
   if you try and access them from the constructor. This is actually how annotating JavaFX
   fields with `@FXML` works too!

> **Warning**
> Currently the dependency injection doesn't support circular dependencies. I.e. if you had 2
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
> When it tries to create class A it will see that it needs an instance of B, so it will try to
> create an instance of B, however, to create B it needs an instance of A, so it tries to create
> an instance of A and so the cycle continues. This infinite loop will eventually cause a stack
> overflow error.

### Singletons

By default, whenever we need to inject a dependency into a class, it will create a completely
new instance. However, most of the time we want to reuse the same instance of a class whenever
we inject it. This is particularly important if we're creating classes that act as communication
interfaces between controllers. This is where the singleton design pattern comes in.
It ensures that only one instance of a class will be created and that the instance will be
reused whenever we need to inject it. To make a class a singleton, we need to annotate it with
`@Singleton`. This will tell the dependency injection to only create one instance of the class
and then store it for future use.

## Controller Lifecycle

There are 2 interfaces that **controllers** can implement to respond to certain events.

1. `LoadListener` - This requires you to implement an `onLoad` function which will be called
   **everytime** the view associated with that controller is switched to. This is the method 
   where you should initialise/reset UI which can be changed while the user was previously on 
   the same view. For example, with the canvas view after you complete a drawing, a button that 
   allows you to start a new game is displayed. When we switch back to the
   canvas view again, in this method we can reset it to make this button invisible.

> **Note**
> The `initialize` method is only called once when the controller is first created and should be
> used for one-off initialisations, such as creating instances of classes that are used by the
> controller.

2. `TerminationListener` - This requires you to implement an `onTerminate` method that is called
   just before the application is closed. This is where any termination of processes or saving 
   of data should be handled.

> **Note**
> After [#9](https://github.com/SOFTENG206-2022/quick-draw-beta-final-team-09/issues/9), this will
> change to use a non-static `SceneManager` instance which can be
> injected, so
> it will instead look like: `this.sceneManager.switchToView(View.CANVAS)`,
> where `this.sceneManager`
> is an injected field.

### Ordering of Lifecycle

1. Constructor dependencies are injected
2. Field dependencies are injected
3. `initialize` is called (Only occurs once)
4. `onLoad` is called (This happens whenever the view is switched to)
5. `onTerminate` is called (This happens just before the application is closed)

## Switching Views

Switching between views is very easy. Each JavaFX view should have an associated value in the 
`View` enum. By default, the `.fxml` file name for the view is the same as the name of the enum 
but in lowercase. We can then switch between views whenever we need by calling `SceneManager.
getInstance().switchToView(View.CANVAS)`.

## Putting it all Together

Let's consider an example where we want to create a `WordService` that manages the current word 
being drawn. We want this class to be a singleton so that we can access the same instance of it 
from two different controllers. One controller will be responsible for generating a new random 
word everytime it's view is switched to and the other will need to be able to read it and 
display it in a label. We can do that by creating a class like:

```java
import java.awt.Label;
import javafx.fxml.FXML;
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

public class ControllerA implements LoadListener {

  @Inject private WordService wordService;

  @Override
  public void onLoad() {
    // Generate a random word everytime the view associated with this controller is switched to
    this.wordService.generateRandomWord();
  }
}

public class ControllerB implements LoadListener{

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