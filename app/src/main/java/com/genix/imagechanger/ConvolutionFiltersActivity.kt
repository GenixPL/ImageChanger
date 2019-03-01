package com.genix.imagechanger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class ConvolutionFiltersActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_convolution_filters)
		supportActionBar!!.hide()

		initButtons()
	}

	private fun initButtons() {

	}



	/* OTHER METHODS */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}