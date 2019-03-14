package com.genix.imagechanger

import android.content.Intent
import android.graphics.*
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.RenderScript
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_lab.*


class LabActivity : AppCompatActivity() {

	private var rsContext : RenderScript? = null
	private var numberOfShades = 5
	private var thresholdValue = 0.5f


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_lab)
		supportActionBar!!.hide()

		setImageView()
		initButtons()

		rsContext = RenderScript.create(this)
	}

	override fun onResume() {
		setImageView()
		super.onResume()
	}

	private fun setImageView() {
		labView.setImageBitmap(MainActivity.currentImage)
	}

	private fun initButtons() {
		labButton.setOnClickListener { BackgroundGreyLevels().execute() }
		ditheringButton.setOnClickListener {
			startActivity(Intent(this, OrderedDitheringActivity::class.java))
		}
	}



	/* CLASSES FOR RS */
	inner class BackgroundGreyLevels : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap :Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val labScript = ScriptC_grey_levels(rsContext)
			labScript._numberOfShades = numberOfShades
			labScript._thresholdValue = thresholdValue

			labScript.forEach_grey_levels(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Grey levels is done")
			MainActivity.currentImage = result
			setImageView()
		}
	}



	/* OTHER METHODS */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}
