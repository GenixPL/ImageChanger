package com.genix.imagechanger.task3

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.floor

fun putPixel(img: Bitmap, x: Int, y: Int, c: Double) {
	val col = (Color.BLACK * c).toInt()
	img.setPixel(x, y, col)
}

fun iPart(n: Double): Int {
	return floor(n).toInt()
}

fun round(n: Double): Int {
	return iPart(n + 0.5)
}

fun fPart(n: Double): Double {
	return n - iPart(n)
}

fun rfPart(n: Double): Double {
	return 1 - fPart(n)
}

/**
 * https://en.wikipedia.org/wiki/Xiaolin_Wu's_line_algorithm
 */
fun drawWuLine(img: Bitmap, x1: Int, y1: Int, x2: Int, y2: Int) {
	val steep = abs(y2 - y1) > abs(x2 - x1)
	var x1 = x1.toDouble()
	var y1 = y1.toDouble()
	var x2 = x2.toDouble()
	var y2 = y2.toDouble()


	if (steep) {
		var temp = x1
		x1 = y1
		y1 = temp

		temp = x2
		x2 = y2
		y2 = temp
	}

	if (x1 > x2) {
		var temp = x1
		x1 = x2
		x2 = temp

		temp = y1
		y1 = y2
		y2 = temp
	}

	val dx = x2 - x1
	val dy = y2 - y1
	var gradient = dy / dx

	if (dx == 0.0) {
		gradient = 1.0
	}

	// handle first endpoint
	var xEnd = round(x1)
	var yEnd = y1 + gradient * (xEnd - x1)
	var xGap = rfPart(x1 + 0.5)
	val xPxl1 = xEnd // this will be used in the main loop
	val yPxl1 = iPart(yEnd)
	if (steep) {
		putPixel(img, yPxl1, xPxl1, rfPart(yEnd) * xGap)
		putPixel(img, yPxl1 + 1, xPxl1, fPart(yEnd) * xGap)

	} else {
		putPixel(img, xPxl1, yPxl1, rfPart(yEnd) * xGap)
		putPixel(img, xPxl1, yPxl1 + 1, fPart(yEnd) * xGap)
	}

	var intery = (yEnd + gradient) // first y-intersection for the main loop

	// handle second endpoint
	xEnd = round(x2)
	yEnd = y2 + gradient * (xEnd - x2)
	xGap = fPart(x2 + 0.5)
	val xPxl2 = xEnd //this will be used in the main loop
	val yPxl2 = iPart(yEnd)
	if (steep) {
		putPixel(img, yPxl2, xPxl2, rfPart(yEnd) * xGap)
		putPixel(img,yPxl2 + 1, xPxl2, fPart(yEnd) * xGap)

	} else {
		putPixel(img, xPxl2, yPxl2, rfPart(yEnd) * xGap)
		putPixel(img, xPxl2, yPxl2 + 1, fPart(yEnd) * xGap)
	}

	// main loop
	if (steep) {
		for (i in xPxl1 + 1 until xPxl2) {
			putPixel(img, iPart(intery), i, rfPart(intery))
			putPixel(img, iPart(intery) + 1, i, fPart(intery))
			intery += gradient
		}

	} else {
		for (i in xPxl1 + 1 until xPxl2) {
			putPixel(img, i, iPart(intery), rfPart(intery))
			putPixel(img, i, iPart(intery) + 1, fPart(intery))
			intery += gradient
		}
	}
}