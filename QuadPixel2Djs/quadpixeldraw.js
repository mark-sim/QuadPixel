
// Image Drop Handler
function dropHandler(ev) {
  console.log('File(s) dropped');

  // Prevent default behavior (Prevent file from being opened)
  ev.preventDefault();

  if (ev.dataTransfer.items) {
    // Use DataTransferItemList interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.items.length; i++) {
      // If dropped items aren't files, reject them
      if (ev.dataTransfer.items[i].kind === 'file') {
        var file = ev.dataTransfer.items[i].getAsFile();
        displayImage(file);
        console.log('... file[' + i + '].name = ' + file.name);
      }
    }
  } else {
    // Use DataTransfer interface to access the file(s)
    for (var i = 0; i < ev.dataTransfer.files.length; i++) {
      console.log('... file[' + i + '].name = ' + ev.dataTransfer.files[i].name);
    }
  } 
  
  // Pass event to removeDragData for cleanup
  removeDragData(ev)
}

function dragOverHandler(ev) {
  console.log('File(s) in drop zone'); 

  // Prevent default behavior (Prevent file from being opened)
  ev.preventDefault();
}


function removeDragData(ev) {
  console.log('Removing drag data')

  if (ev.dataTransfer.items) {
    // Use DataTransferItemList interface to remove the drag data
    ev.dataTransfer.items.clear();
  } else {
    // Use DataTransfer interface to remove the drag data
    ev.dataTransfer.clearData();
  }
}

function displayImage(file) {
	console.log('displaying image: ' + file.name);
	var canvas;
	var reader  = new FileReader();
    // it's onload event and you forgot (parameters)
    reader.onload = function(e)  {
        var img = document.createElement("img");
        // the result image data
        img.src = e.target.result;

        img.onload = function() {
        canvas = document.createElement("canvas");
        //var canvas = $("<canvas>", {"id":"testing"})[0];
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0);

        var MAX_WIDTH = 1100;
        var MAX_HEIGHT = 1100;
        var width = img.width;
        var height = img.height;

        console.log("width" + width + "height" + height);
        if (width > height) {
          if (width > MAX_WIDTH) {
            height *= MAX_WIDTH / width;
            width = MAX_WIDTH;
          }
        } else {
          if (height > MAX_HEIGHT) {
            width *= MAX_HEIGHT / height;
            height = MAX_HEIGHT;
          }
        }
        canvas.width = width;
        canvas.height = height;
        var ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, width, height);

		console.log('canvas width, height: ' + canvas.width + ', ' + canvas.height);

        var dataurl = canvas.toDataURL(file.type);

        //document.getElementById('image').src = dataurl;     

        document.getElementById('output').src = dataurl;
     	}
     }
     // you have to declare the file loading
     reader.readAsDataURL(file);

    // wait for specified ms before running.
    var millisecondsToWait = 1000;
	setTimeout(function() {
   		start(file.type, canvas);
	}, millisecondsToWait);
}

// QuadPixel Logic.
function clone(arr) {
	var retArr = [];
	for (var i=0; i<arr.length; i++)
	{
		retArr.push(arr[i]);
	}
	console.log(arr);
	console.log(retArr);
	return retArr;
}

function setBlackBackground(newImage, leftX, rightX, bottomY, topY) {
	for (var x = this.leftX; x <= this.rightX; ++x) {
		for (var y = this.bottomY; y <= this.topY; ++y) {
			var index = (x + (y * this.width)) * 4;
			this.newImage.data[index] = 255;
			this.newImage.data[index + 1] = 255;
			this.newImage.data[index + 2] = 255;
			this.newImage.data[index + 3] = 255;
		}
	}
}

function start(fileType, canvas) {
	//var canvas = document.createElement('canvas');
	var img = document.getElementById('output');

	//canvas.width = img.width;
	//canvas.height = img.height;
	console.log('canvas width and height' + '(' + canvas.width + ',' + canvas.height + ')' + ',' + 'img width and height' + '(' + img.width + ',' + img.height + ')');
	var context = canvas.getContext('2d');
	context.drawImage(img, 0, 0);
	var newImage = context.createImageData(canvas.width, canvas.height);
	var arr = context.getImageData(0, 0, canvas.width, canvas.height);
	var pixels = clone(arr.data);

	var quadPixel = new QuadPixel(pixels, arr, canvas.width, canvas.height, fileType);

	var highestMeanErrorQuadrant = quadPixel.processImage(0, canvas.width - 1, 0, canvas.height - 1);

	context.clearRect(0, 0, canvas.width, canvas.height);
	context.putImageData(highestMeanErrorQuadrant.newImage, 0, 0);

	var dataurl = canvas.toDataURL(fileType);

	//setBlackBackground(newImage, 0, canvas.width - 1, 0, canvas.height - 1);

    document.getElementById('output').src = dataurl;

    //test2(highestMeanErrorQuadrant, fileType, canvas, context, quadPixel)

    	setInterval(function() {
		console.log('meanError: ' + Math.floor(highestMeanErrorQuadrant.meanError));

    	highestMeanErrorQuadrant = quadPixel.processImage(highestMeanErrorQuadrant.leftX, highestMeanErrorQuadrant.rightX, highestMeanErrorQuadrant.bottomY, highestMeanErrorQuadrant.topY);

    	context.clearRect(0, 0, canvas.width, canvas.height);
		context.putImageData(highestMeanErrorQuadrant.newImage, 0, 0);
		var dataurl = canvas.toDataURL(fileType);

    		//document.getElementById('image').src = dataurl;     
		document.getElementById('output').src = dataurl;
		}, 50);
    
}

function test2(highestMeanErrorQuadrant, fileType, canvas, context, quadPixel) {

    while (Math.floor(highestMeanErrorQuadrant.meanError) >= 500000)
    {
		console.log('meanError: ' + Math.floor(highestMeanErrorQuadrant.meanError));

    	highestMeanErrorQuadrant = quadPixel.processImage(highestMeanErrorQuadrant.leftX, highestMeanErrorQuadrant.rightX, highestMeanErrorQuadrant.bottomY, highestMeanErrorQuadrant.topY);

    	context.clearRect(0, 0, canvas.width, canvas.height);
		context.putImageData(highestMeanErrorQuadrant.newImage, 0, 0);
		var dataurl = canvas.toDataURL(fileType);

    		//document.getElementById('image').src = dataurl;     
		document.getElementById('output').src = dataurl;

		window.requestAnimationFrame(function() { test2(highestMeanErrorQuadrant, fileType, canvas, context);});
    }
}

// QuadPixel Class

function QuadPixel(pixels, newImage, width, height, fileType) {
	this.pixels = pixels;
	this.newImage = newImage;
	this.width = width;
	this.height = height;
	this.fileType = fileType;
	this.pq = new PriorityQueue({ comparator : function(a,b) { return b.meanError - a.meanError }});
}

QuadPixel.prototype.processImage = function(leftX, rightX, bottomY, topY) {
	var highestMeanErrorQuadrant = this.process(leftX, rightX, bottomY, topY);
	return highestMeanErrorQuadrant; 
}

QuadPixel.prototype.process = function(leftX, rightX, bottomY, topY) {
	var adjustedWidth = rightX - leftX + 1;
	var adjustedHeight = topY - bottomY + 1;

	var NW = new Quadrant(this.newImage, this.pixels, this.width, this.height, leftX, leftX + Math.floor(adjustedWidth / 2) - 1, bottomY, bottomY + Math.floor(adjustedHeight / 2) - 1);
	var NE = new Quadrant(this.newImage, this.pixels, this.width, this.height, leftX + Math.floor(adjustedWidth / 2) + 1, rightX, bottomY, bottomY + Math.floor(adjustedHeight / 2 ) - 1);
	var SE = new Quadrant(this.newImage, this.pixels, this.width, this.height, leftX + Math.floor(adjustedWidth / 2) + 1, rightX, bottomY + Math.floor(adjustedHeight / 2) + 1, topY);
	var SW = new Quadrant(this.newImage, this.pixels, this.width, this.height, leftX, leftX + Math.floor(adjustedWidth / 2) - 1, bottomY + Math.floor(adjustedHeight / 2) + 1, topY);

	NW.calculateSquaredMeanError(NW.averageQuadrant());
	NE.calculateSquaredMeanError(NE.averageQuadrant());
	SE.calculateSquaredMeanError(SE.averageQuadrant());
	SW.calculateSquaredMeanError(SW.averageQuadrant());

	NW.processQuadrant();
	NE.processQuadrant();
	SE.processQuadrant();
	SW.processQuadrant();

	this.pq.queue(NW);
	this.pq.queue(NE);
	this.pq.queue(SE);
	this.pq.queue(SW);

	this.setBlackLine(leftX, rightX, bottomY, topY, leftX + Math.floor(adjustedWidth / 2), bottomY + Math.floor(adjustedHeight / 2));

	return this.pq.dequeue();
}

QuadPixel.prototype.setBlackLine = function(leftX, rightX, bottomY, topY, midX, midY) {
	console.log('setting black line: ' + leftX + ',' + rightX + ',' + bottomY + ',' +  topY + ',' +  midX + ',' +  midY);
	for (var y = bottomY; y <= topY; ++y) {
		var index = (midX + (y * this.width)) * 4;
		this.newImage.data[index] = 0;
		this.newImage.data[index + 1] = 0;
		this.newImage.data[index + 2] = 0;
		this.newImage.data[index + 3] = 0;
	}

	for (var x = leftX; x <= rightX; ++x) {
		var index = (x + (midY * this.width)) * 4;
		this.newImage.data[index] = 0;
		this.newImage.data[index + 1] = 0;
		this.newImage.data[index + 2] = 0;
		this.newImage.data[index + 3] = 0;
	}
}

// Quadrant Class

function Quadrant(newImage, pixels, width, height, leftX, rightX, bottomY, topY) {
	//console.log('added X: (' + leftX + ' , ' + rightX + ') Y: (' + bottomY + ' , ' + topY + ')');
	this.newImage = newImage;
	this.pixels = pixels;
	this.width = width;
	this.height = height;
	this.leftX = leftX;
	this.rightX = rightX;
	this.bottomY = bottomY;
	this.topY = topY;
}

Quadrant.prototype.setQuadrant = function(averageColor) {
	var r = averageColor[0];
	var g = averageColor[1];
	var b = averageColor[2];
	var a = averageColor[3];

	//console.log('setting color r:' + r + ' g:' + g + ' b:' + b + ' a:' + a);
	//console.log('Setting X: (' + this.leftX + ' , ' + this.rightX + ') Y: (' + this.bottomY + ' , ' + this.topY + ')');

	for (var x = this.leftX; x <= this.rightX; ++x) {
		for (var y = this.bottomY; y <= this.topY; ++y) {
			var index = (x + (y * this.width)) * 4;
			this.newImage.data[index] = r;
			this.newImage.data[index + 1] = g;
			this.newImage.data[index + 2] = b;
			this.newImage.data[index + 3] = a;
		}
	}
}

Quadrant.prototype.calculateSquaredMeanError = function(average) {
	var r = average[0];
	var g = average[1];
	var b = average[2];
	var a = average[3];
	var meanError = 0;
	var adjustedWidth = this.rightX - this.leftX + 1;
	var adjustedHeight = this.topY - this.bottomY + 1;
	var totalPixel = adjustedWidth * adjustedHeight;
	if (totalPixel == -1)
	{
		this.meanError = -1;
		return;
	}

	for (var x = this.leftX; x <= this.rightX; ++x) {
		for (var y = this.bottomY; y <= this.topY; ++y) {
			var index = (x + (y * this.width)) * 4;
			meanError += Math.pow((r - this.pixels[index]), 2);
			meanError += Math.pow((g - this.pixels[index + 1]), 2);
			meanError += Math.pow((b - this.pixels[index + 2]), 2);
			meanError += Math.pow((a - this.pixels[index + 3]), 2);
		}
	}

	this.meanError = meanError;
}

Quadrant.prototype.averageQuadrant = function() {
	var adjustedWidth = this.rightX - this.leftX + 1;
	var adjustedHeight = this.topY - this.bottomY + 1;
	var totalPixel = adjustedWidth * adjustedHeight;
	var totalAlphaValue = 0;
	var totalRedValue = 0;
	var totalGreenValue = 0;
	var totalBlueValue = 0;

	if (totalPixel == 0) {
		return -1;
	}

	for (var x = this.leftX; x <= this.rightX; ++x) {
		for (var y = this.bottomY; y <= this.topY; ++y) {
			var index = (x + (y * this.width)) * 4;
			totalRedValue += this.pixels[index];
			totalGreenValue += this.pixels[index + 1];
			totalBlueValue += this.pixels[index + 2];
			totalAlphaValue += this.pixels[index + 3];
		}
	}

	var averageRedValue = totalRedValue / totalPixel;
	var averageGreenValue = totalGreenValue / totalPixel;
	var averageBlueValue = totalBlueValue / totalPixel;
	var averageAlphaValue = totalAlphaValue / totalPixel;

	var ret = [averageRedValue, averageGreenValue, averageBlueValue, averageAlphaValue];
	this.averageColor = ret;
	return ret; 
}

Quadrant.prototype.processQuadrant = function() {
	this.setQuadrant(this.averageColor);
}