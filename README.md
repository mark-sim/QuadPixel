## QuadPixel

Computer art based on quadtrees.

The program targets an input image. The input image is split into four quadrants. Each quadrant is assigned an averaged color based on the colors in the input image. The quadrant with the largest error is split into its four children quadrants to refine the image. This process is repeated N times.

### Web Demo

A web-based version can be seen here:

https://marksim5.github.io/QuadPixel.html

### Installation
If you want to try out QuadPixel in Java (Output image has better quality), Install
* [Maven](https://maven.apache.org/download.cgi)
* [JDK 1.7+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
```
git clone https://github.com/marksim5/QuadPixel.git

mvn clean install -DskipTests

java -jar target/QuadPixel2D-1.0-SNAPSHOT-jar-with-dependencies.jar fullPathToImage
```

### Animation


### Samples
<a href="url"><img src="https://github.com/marksim5/QuadPixel/blob/master/sample/sol.png" align="left" style="max-width:100%;" ></a>
<br>
<br>
<a href="url"><img src="https://github.com/marksim5/QuadPixel/blob/master/sample/apple.png" align="left" style="max-width:100%;" ></a>
<br>
<br>
<a href="url"><img src="https://github.com/marksim5/QuadPixel/blob/master/sample/pokeball.png" align="left" style="max-width:100%;" ></a>
<br>
<br>
<a href="url"><img src="https://github.com/marksim5/QuadPixel/blob/master/sample/luigi.png" align="left" style="max-width:100%;" ></a>
