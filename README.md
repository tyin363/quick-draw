# SE206 - 2022 - Beta & Final Releases

## Controller Lifecycle

There are 2 interfaces that controllers can implement to respond to certain events.

1. `LoadListener` - This requires you to implement an `onLoad` function which will be called **
   everytime** the view
   associated with that controller is switched to. This is the method where you should
   initialise/reset UI which can
   be changed while the user was previously on the same view. For example, with the canvas view
   after you complete a
   drawing, a button that allows you to start a new game is displayed. When we switch back to the
   canvas view again,
   in this method we can reset it to make this button invisible.

> **Note**
> The `initialize` method is only called once when the controller is first created and should be
> used for one-off initialisations, such as creating instances of classes that are used by the
> controller.

2. `TerminationListener` - This requires you to implement an `onTerminate` method that is called
   just before the
   application is closed. This is where any termination of processes or saving of data should be
   handled.

## Switching Views

Switching between views is very easy. Each JavaFX view has an associated value in the `View` enum.
By default, the `.
fxml` file name for the view is the same as the name of the enum but in lowercase. We can then
switch between views
whenever we need by calling `SceneManager.getInstance().switchToView(View.CANVAS)`.

> **Note**
> After [#9](https://github.com/SOFTENG206-2022/quick-draw-beta-final-team-09/issues/9), this will
> change to use a non-static `SceneManager` instance which can be
> injected, so
> it will instead look like: `this.sceneManager.switchToView(View.CANVAS)`,
> where `this.sceneManager`
> is an injected field.

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