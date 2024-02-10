# CSCI-576-ASSIGNMENT-1
A practical understanding of Spatial/Temporal Sampling and Filtering in terms of how these processes affect visual media types like images and video.


## Image Display with Triple Buffering

This Java program provides a simple image display application utilizing triple buffering for smooth animation. It allows for rotating and scaling an input image with specified parameters such as angle, scale, and frame rate.

## Features

- Rotates and scales an input image smoothly using triple buffering.
- Provides options to specify rotation angle, scaling factor, and frame rate.
- Supports smooth animation rendering at 30 frames per second (FPS).

## Usage

1. Compile the Java source code using any Java compiler.
2. Run the compiled program with the following command-line arguments:
    - Argument 1: Path to the input image file.
    - Argument 2: Scaling factor (double).
    - Argument 3: Rotation angle in degrees (double).
    - Argument 4: Frame rate (integer).

Example:
```
javac ImageDisplay.java
java ImageDisplay Lena_512_512.rgb 1.5 45 30
```
## Dependencies

- Java Development Kit (JDK) 8 or higher.
- Java Swing library for GUI components.

## Implementation Details

- The program reads the input image in RGB format and stores it in a pixel matrix.
- It utilizes triple buffering for smooth animation rendering.
- The `animate()` method performs rotation and scaling on each pixel of the input image.
- A game loop logic is implemented to achieve the desired frame rate.

## Author

Mahesh Joseph Sadashiv
