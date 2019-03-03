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
import kotlinx.android.synthetic.main.activity_functional_filters.*


class ConvolutionFiltersActivity : AppCompatActivity() {

	private var rsContext : RenderScript? = null
	private var pixels : IntArray? = null
	private var divisors: FloatArray? = FloatArray(9) { 0.11f }
	private var matrixWidth = 3
	private var matrixHeight = 3
	private var offset = 0


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_convolution_filters)
		supportActionBar!!.hide()

		rsContext = RenderScript.create(this)

		initPixels()
		setImageView()
		initButtons()
	}

	private fun initPixels() {
		var width = MainActivity.currentImage!!.width
		var height = MainActivity.currentImage!!.height
		var size = width * height
		pixels = IntArray(size)

		MainActivity.currentImage!!.getPixels(pixels, 0, width, 0, 0, width, height)
	}

	private fun setImageView() {
		convolutionImageView.setImageBitmap(MainActivity.currentImage)
	}

	private fun initButtons() {
		blurButton.setOnClickListener { BackgroundBlur().execute() }
	}



	/* OTHER METHODS */
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
			blur._matrix_height = MainActivity.currentImage!!.height
			blur._matrix_width = matrixWidth
			blur._matrix_height = matrixHeight
			blur._offset = offset

			//assign pixels (from here) to *pixels in .rs
			var intArrayBuilder = Type.Builder(rsContext, Element.I32(rsContext))
			intArrayBuilder.setX(pixels!!.size)
			var pxAlloc = Allocation.createTyped(rsContext, intArrayBuilder.create())
			pxAlloc.copyFrom(pixels)
			blur.bind_pixels(pxAlloc)

			//assign divisors (from here) to *divisors in .rs
			var floatArrayBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			floatArrayBuilder.setX(divisors!!.size)
			var divAlloc = Allocation.createTyped(rsContext, floatArrayBuilder.create())
			divAlloc.copyFrom(divisors)
			blur.bind_divisors(divAlloc)

			blur.forEach_blur(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Blur is done")
			MainActivity.currentImage = result
			setImageView()
		}
	}
}