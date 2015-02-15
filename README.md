Orx Animation Editor
====================

![Screenshot](https://bitbucket.org/orx/animationeditor/raw/screenshots/screenshot.png "Screenshot")

The Orx Animation Editor has been created with a hope to satisfy the need for minimal animation editing without touching Orx config files. Skeletal animations are, as of now, out of the scope of this editor.

### Main Features:
* Create animations and frames
* Create animation sets (links, etc...)
* Edit various properties of frames graphically
* Edit (and visualize) offsets for each frame
* Visualize and organize animations and links inside an animation set
* View how a frame, animation or a sequence of them look
* Write all of these to a target config file.
* NOTE: It is not possible to parse an .ini file. But you can save the current editor data in a special format and load it later on. The saved editor state can be moved around in the filesystem together with the referenced files and it will keep functioning correctly.
* Help section

### Building:
Orx Animation Editor is developed using java and it uses [Gradle](http://gradle.org/) as the build system. In order to compile the project and obtain a runnable jar, all one needs to do is:

* Install [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (you should be able to run `javac` in your terminal)
* At the repository root run `./gradlew jar` (*nix) or `gradlew.bat jar` (Windows)

At this point you should have the runnable jar under the `<repo-root>/build/libs` folder.
