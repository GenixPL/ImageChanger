package com.genix.imagechanger

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.Type
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_convolution_filters.*


const val SHARPEN_A = 1
const val SHARPEN_B = 5

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
	 * Array of weights assigned to each cell of matrix
	 *
	 * @default: 0.11 (1/9 - equally weighted)
	 */
	private var matrix: FloatArray? = null

	/**
	 * Divisor - value by which we divide colors received during computations
	 */
	private var divisor: Float? = null

	/**
	 * Width of matrix used in convolution filter
	 */
	private val matrixWidth : Int? = null
	private var defaultMatrixWidth = 3

	/**
	 * Height of matrix used in convolution filter
	 */
	private var matrixHeight : Int? = null
	private var defaultMatrixHeight = 3

	/**
	 * Offset - value added to each color
	 */
	private var offset : Float? = null
	private var defaultOffset = 0

	/**
	 * Array of default (3x3 matrix) changes in coordinates (looking from anchor point of view) which should be used for given matrix
	 * e.g. [(x1) -1, (y1) -1, (x2) 0, (y2) -1, ...] means that for given (x, y) pixel we take following pixels into account
	 * (x1, y1):(x+(-1), y+(-1)), (x2, y2):(x+(0), (y+(-1)), ...
	 */
	private var changesInCoordinatesToConsider : IntArray? = null
	private var defaultChangesInCoordinatesToConsider = intArrayOf(-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1)


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
		blurButton.setOnClickListener { BackgroundBlur().execute() }
		gaussianButton.setOnClickListener { BackgroundGaussianSmoothing().execute() }
		sharpenButton.setOnClickListener { BackgroundSharpening().execute() }
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
			val blur = ScriptC_convolution(rsContext)
			blur._bitmap_width = MainActivity.currentImage!!.width
			blur._bitmap_height = MainActivity.currentImage!!.height
			blur._matrix_width = defaultMatrixWidth
			blur._matrix_height = defaultMatrixHeight
			blur._offset = defaultOffset
			blur._divisor = 8f //in case of default gaussian smoothing it's 8

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
			matrixAlloc.copyFrom(floatArrayOf(0f, 1f, 0f, 1f, 4f, 1f, 0f, 1f, 0f)) //in case of default gaussian smoothing matrix consists of ones
			blur.bind_matrix(matrixAlloc)

			blur.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Gaussian smoothing is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
		}
	}

	inner class BackgroundSharpening : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap : Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			val def1 = -1f * SHARPEN_A / (SHARPEN_B - 4 * SHARPEN_A)
			val def2 = 1f * SHARPEN_B / (SHARPEN_B - 4 * SHARPEN_A)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val convolution = ScriptC_convolution(rsContext)
			convolution._bitmap_width = MainActivity.currentImage!!.width
			convolution._bitmap_height = MainActivity.currentImage!!.height
			convolution._matrix_width = defaultMatrixWidth
			convolution._matrix_height = defaultMatrixHeight
			convolution._offset = defaultOffset
			convolution._divisor = 4 * def1 + def2 //in case of default sharpening

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
			matrixAlloc.copyFrom(floatArrayOf(0f, def1, 0f, def1, def2, def1, 0f, def1, 0f)) //in case of default sharpening
			convolution.bind_matrix(matrixAlloc)

			convolution.forEach_convolution(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Sharpening is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
		}
	}
}