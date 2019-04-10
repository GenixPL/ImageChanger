package com.genix.imagechanger.task3

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.genix.imagechanger.MainActivity
import com.genix.imagechanger.R
import kotlinx.android.synthetic.main.activity_task3.*

class Task3Activity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_task3)
		supportActionBar!!.hide()

		setImageView()
		initButtons()
	}

	override fun onPostResume() {
		setImageView()
		super.onPostResume()
	}

	private fun initButtons() {
		drawLineButton.setOnClickListener { moveToDrawLineActivity() }
	}

	private fun moveToDrawLineActivity() {
		startActivity(Intent(this, DrawLineActivity::class.java))
	}

	private fun setImageView() {
		task3ImageView.setImageBitmap(MainActivity.currentImage)
	}

}
