OrxAnimationEditor
==================

An animation editor for the orx game engine

So, what you can do is:
* Define a target .ini file
* Create animations
* Create frames
* Delete them as well
* Open an image
* Zoom to image
* Define a segment of the image as the selected frame (left-click and drag on the image)
* Open an image associated with the selected frame (spacebar key)
* Define the pivot (right click on the image)
* Edit the x,y flips (Edit menu)
* Edit the default key duration, and the key durations for the animations and frames respectively (Edit menu)
* View how a frame looks
* View how an animation looks
* When you're done, you can write to or append to your target.ini file. The image file paths are written relative to the .ini path.
* It is not possible to parse an .ini file, but the state of the editor can be separately saved/loaded. The output is not an .ini file.

- The editor does not yet support animation sets and animation transitions. I intend to implement a graphical transition editor in the not immediate future. Feel free to beat me to it.
