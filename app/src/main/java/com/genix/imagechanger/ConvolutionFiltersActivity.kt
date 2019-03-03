package com.genix.imagechanger

import android.content.Intent
import android.graphics.Bitmap
import android.opengl.Visibility
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.Type
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_convolution_filters.*


class ConvolutionFiltersActivity : AppCompatActivity() {

	/**
	 * Context used in functions related to RenderScript
	 */
	private var rsContext : RenderScript? = null

	/**
	 * Array of all pixels of current bitmap from MainActivity
	 */
	private var pixels : IntArray? = null

	/**
	 * Width of matrix used in convolution filter
	 */
	private var defaultMatrixWidth = 3

	/**
	 * Height of matrix used in convolution filter
	 */
	private var defaultMatrixHeight = 3

	/**
	 * Offset - value added to each color
	 */
	private var defaultOffset = 0

	/**
	 * Array of default (3x3 matrix) changes in coordinates (looking from anchor point of view) which should be used for given matrix
	 * e.g. [(x1) -1, (y1) -1, (x2) 0, (y2) -1, ...] means that for given (x, y) pixel we take following pixels into account
	 * (x1, y1):(x+(-1), y+(-1)), (x2, y2):(x+(0), (y+(-1)), ...
	 */
	private var defaultChangesInCoordinatesToConsider = intArrayOf(-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1)

	/**
	 * Those fields are changed in EditCustomMatrixActivity
	 */
	companion object {

		/**
		 * Divisor - value by which we divide colors received during computations
		 */
		var divisor = 1.0f

		/**
		 * Array of weights assigned to each cell of matrix
		 */
		var matrix = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)

		var matrixWidth = 3
		var matrixHeight = 3
		var offset = 0
		var changesInCoordinatesToConsider = intArrayOf(-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1)
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_convolution_filters)
		supportActionBar!!.hide()

		rsContext = RenderScript.create(this)

		setImageViewAndPixels()
		initButtons()
	}

	/**
	 * Initialize array of pixels using current bitmap from MainActivity
	 */
	private fun setPixels() {
		var width = MainActivity.currentImage!!.width
		var height = MainActivity.currentImage!!.height
		var size = width * height
		pixels = IntArray(size)

		MainActivity.currentImage!!.getPixels(pixels, 0, width, 0, 0, width, height)
	}

	private fun setImageViewAndPixels() {
		convolutionImageView.setImageBitmap(MainActivity.currentImage)
		setPixels()
	}

	private fun initButtons() {
		blurButton.setOnClickListener {
			BackgroundBlur().execute()
			progressBar.visibility = View.VISIBLE
		}

		gaussianButton.setOnClickListener {
			BackgroundGaussianSmoothing().execute()
			progressBar.visibility = View.VISIBLE
		}

		sharpenButton.setOnClickListener {
			BackgroundSharpening().execute()
			progressBar.visibility = View.VISIBLE
		}

		edgeButton.setOnClickListener {
			BackgroundEdgeDetection().execute()
			progressBar.visibility = View.VISIBLE
		}

		embossButton.setOnClickListener {
			BackgroundEmbossing().execute()
			progressBar.visibility = View.VISIBLE
		}

		applyCustomButton.setOnClickListener {
			BackgroundCustom().execute()
			progressBar.visibility = View.VISIBLE
		}


		editCustomMatrixButton.setOnClickListener {
			startActivity(Intent(this, EditCustomMatrixActivity::class.java))
		}
	}


	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}


	inner class BackgroundBlur : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val blur = ScriptC_convolution(rsContext)
			blur._bitmap_width = MainActivity.currentImage!!.width
			blur._bitmap_height = MainActivity.currentImage!!.height
			blur._matrix_width = defaultMatrixWidth
			blur._matrix_height = defaultMatrixHeight
			blur._offset = defaultOffset
			blur._divisor = 9f //in case of default blur it's 9

			//assign pixels (from here) to *pixels in convolution.rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			blur.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in convolution.rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(defaultChangesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(defaultChangesInCoordinatesToConsider)
			blur.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in convolution.rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(defaultMatrixHeight * defaultMatrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(FloatArray(9) { 1f }) //in case of default blur matrix consists of ones
			blur.bind_matrix(matrixAlloc)

			blur.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Blur is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundGaussianSmoothing : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val convolution = ScriptC_convolution(rsContext)
			convolution._bitmap_width = MainActivity.currentImage!!.width
			convolution._bitmap_height = MainActivity.currentImage!!.height
			convolution._matrix_width = defaultMatrixWidth
			convolution._matrix_height = defaultMatrixHeight
			convolution._offset = defaultOffset
			convolution._divisor = 8f //in case of default gaussian smoothing it's 8

			//assign pixels (from here) to *pixels in convolution.rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			convolution.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in convolution.rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(defaultChangesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(defaultChangesInCoordinatesToConsider)
			convolution.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in convolution.rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(defaultMatrixHeight * defaultMatrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(floatArrayOf(0f, 1f, 0f, 1f, 4f, 1f, 0f, 1f, 0f)) //in case of default gaussian smoothing matrix consists of ones
			convolution.bind_matrix(matrixAlloc)

			convolution.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Gaussian smoothing is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundSharpening : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val convolution = ScriptC_convolution(rsContext)
			convolution._bitmap_width = MainActivity.currentImage!!.width
			convolution._bitmap_height = MainActivity.currentImage!!.height
			convolution._matrix_width = defaultMatrixWidth
			convolution._matrix_height = defaultMatrixHeight
			convolution._offset = defaultOffset
			convolution._divisor = 1f //in case of default sharpening

			//assign pixels (from here) to *pixels in .rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			convolution.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in .rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(defaultChangesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(defaultChangesInCoordinatesToConsider)
			convolution.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in .rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(defaultMatrixHeight * defaultMatrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(floatArrayOf(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)) //in case of default sharpening
			convolution.bind_matrix(matrixAlloc)

			convolution.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Sharpening is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundEdgeDetection : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val blur = ScriptC_convolution(rsContext)
			blur._bitmap_width = MainActivity.currentImage!!.width
			blur._bitmap_height = MainActivity.currentImage!!.height
			blur._matrix_width = defaultMatrixWidth
			blur._matrix_height = defaultMatrixHeight
			blur._offset = defaultOffset
			blur._divisor = 0.2222f //in case of default edge detection

			//assign pixels (from here) to *pixels in convolution.rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			blur.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in convolution.rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(defaultChangesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(defaultChangesInCoordinatesToConsider)
			blur.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in convolution.rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(defaultMatrixHeight * defaultMatrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(floatArrayOf(0f, -1f, 0f, 0f, 1f, 0f, 0f, 0f, 0f)) //in case of default edge detection
			blur.bind_matrix(matrixAlloc)

			blur.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Edge detection is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundEmbossing : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val convolution = ScriptC_convolution(rsContext)
			convolution._bitmap_width = MainActivity.currentImage!!.width
			convolution._bitmap_height = MainActivity.currentImage!!.height
			convolution._matrix_width = defaultMatrixWidth
			convolution._matrix_height = defaultMatrixHeight
			convolution._offset = defaultOffset
			convolution._divisor = 1f //in case of default embossing

			//assign pixels (from here) to *pixels in .rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			convolution.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in .rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(defaultChangesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(defaultChangesInCoordinatesToConsider)
			convolution.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in .rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(defaultMatrixHeight * defaultMatrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(floatArrayOf(-1f, 0f, 1f, -1f, 1f, 1f, -1f, 0f, 1f)) //in case of default embossing
			convolution.bind_matrix(matrixAlloc)

			convolution.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Embossing is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundCustom : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val convolution = ScriptC_convolution(rsContext)
			convolution._bitmap_width = MainActivity.currentImage!!.width
			convolution._bitmap_height = MainActivity.currentImage!!.height
			convolution._matrix_width = matrixWidth
			convolution._matrix_height = matrixHeight
			convolution._offset = offset
			convolution._divisor = divisor
			//Log.d("TAG", "w:$matrixWidth, h:$matrixHeight, o:$offset, d:$divisor")

			//assign pixels (from here) to *pixels in .rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			convolution.bind_pixels(pixelsAlloc)

			//assign defaultChangesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in .rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(changesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(changesInCoordinatesToConsider)
			convolution.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in .rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(matrixHeight * matrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(matrix)
			convolution.bind_matrix(matrixAlloc)

			convolution.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Custom filtering is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
			progressBar.visibility = View.INVISIBLE
		}
	}
}