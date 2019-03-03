package com.genix.imagechanger

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.Type
import android.util.Half.toFloat
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
	 * Array of weights assigned to each cell of matrix
	 *
	 * @default: 0.11 (1/9 - equally weighted)
	 */
	private var matrix: FloatArray = FloatArray(9) { 0.111111f }

	/**
	 * Divisor - value by which we divide colors received during computations
	 */
	private var divisor: Float = 1f

	/**
	 * Width of matrix used in convolution filter
	 *
	 * @default: 3
	 */
	private var matrixWidth = 3

	/**
	 * Height of matrix used in convolution filter
	 *
	 * @default: 3
	 */
	private var matrixHeight = 3

	/**
	 * Offset - value added to each color
	 *
	 *  @default: 0
	 */
	private var offset = 0

	/**
	 * Array of changes in coordinates (looking from anchor point of view) which should be used for given matrix
	 * e.g. [(x1) -1, (y1) -1, (x2) 0, (y2) -1, ...] means that for given (x, y) pixel we take following pixels into account
	 * (x1, y1):(x+(-1), y+(-1)), (x2, y2):(x+(0), (y+(-1)), ...
	 *
	 * @default: 3x3 matrix with anchor in the middle
	 */
	private var changesInCoordinatesToConsider = intArrayOf(-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1)


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
			val blur = ScriptC_blur(rsContext)
			blur._bitmap_width = MainActivity.currentImage!!.width
			blur._bitmap_height = MainActivity.currentImage!!.height
			blur._matrix_width = matrixWidth
			blur._matrix_height = matrixHeight
			blur._offset = offset
			blur._divisor = (matrixWidth * matrixHeight).toFloat() //in case of blur it's width*height

			//assign pixels (from here) to *pixels in blur.rs
			var pixelsBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			pixelsBuilder.setX(pixels!!.size)
			var pixelsAlloc = Allocation.createTyped(rsContext, pixelsBuilder.create())
			pixelsAlloc.copyFrom(pixels)
			blur.bind_pixels(pixelsAlloc)

			//assign changesInCoordinatesToConsider (from here) to *changes_in_coordinates_to_consider in blur.rs
			var changesBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			changesBuilder.setX(changesInCoordinatesToConsider.size)
			var changesAlloc = Allocation.createTyped(rsContext, changesBuilder.create())
			changesAlloc.copyFrom(changesInCoordinatesToConsider)
			blur.bind_changes_in_coordinates_to_consider(changesAlloc)

			//assign matrix (from here) to *matrix in blur.rs
			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(matrixHeight * matrixWidth)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(FloatArray(matrixHeight * matrixWidth) { 1f }) //in case of blur matrix consists of ones
			blur.bind_matrix(matrixAlloc)

			blur.forEach_blur(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Blur is done")
			MainActivity.currentImage = result
			setImageViewAndPixels()
		}
	}
}