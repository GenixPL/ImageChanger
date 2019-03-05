package com.genix.imagechanger

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_custom_matrix.*
import kotlinx.android.synthetic.main.activity_functional_filters.*


const val DEFAULT_BRIGHTNESS = 30f
const val DEFAULT_CONTRAST = 1.25f
const val DEFAULT_GAMMA = 1.5f

class FunctionalFiltersActivity : AppCompatActivity() {

	private var rsContext : RenderScript? = null
	private var currentGamma: Float = 1.5f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_functional_filters)
		supportActionBar!!.hide()

		rsContext = RenderScript.create(this)
		gammaEditText.setText(currentGamma.toString())

		setImageView()
		initButtons()
		initEditText()
	}

	private fun initEditText() {
		gammaEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					currentGamma = p0.toString().toFloat()
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
		})
	}

	private fun initButtons() {
		inversionButton.setOnClickListener {
			BackgroundInversion().execute()
			progressBar2.visibility = View.VISIBLE
		}

		brightnessButton.setOnClickListener {
			BackgroundBrightnessCorrection().execute()
			progressBar2.visibility = View.VISIBLE
		}

		contrastButton.setOnClickListener {
			BackgroundContrastEnhancement().execute()
			progressBar2.visibility = View.VISIBLE
		}

		gammaButton.setOnClickListener {
			BackgroundGammaCorrection().execute()
			progressBar2.visibility = View.VISIBLE
		}
	}

	private fun setImageView() {
		functionalImage.setImageBitmap(MainActivity.currentImage)
	}



	/* OTHER METHODS */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}


	/* CLASSES FOR RS */
	inner class BackgroundInversion : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap :Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val inversion = ScriptC_inversion(rsContext)


			inversion.forEach_inversion(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Inversion is done")
			MainActivity.currentImage = result
			setImageView()
			progressBar2.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundBrightnessCorrection : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap :Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut: Allocation = Allocation.createFromBitmap(rsContext, bitmap)

			val brightnessCorrection = ScriptC_brightness(rsContext)
			brightnessCorrection._brightness = DEFAULT_BRIGHTNESS

			brightnessCorrection.forEach_brightness_correction(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Brightness correction is done")
			MainActivity.currentImage = result
			setImageView()
			progressBar2.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundContrastEnhancement : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap :Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val contrast = ScriptC_contrast(rsContext)
			contrast._contrast = DEFAULT_CONTRAST

			contrast.forEach_contrast_enhancement(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Contrast enhancement is done")
			MainActivity.currentImage = result
			setImageView()
			progressBar2.visibility = View.INVISIBLE
		}
	}

	inner class BackgroundGammaCorrection : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap :Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val gamma = ScriptC_gamma(rsContext)
			gamma._gamma = currentGamma

			gamma.forEach_gamma_correction(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Gamma correction is done")
			MainActivity.currentImage = result
			setImageView()
			progressBar2.visibility = View.INVISIBLE
		}
	}
}
