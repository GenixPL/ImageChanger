package com.genix.imagechanger

import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

//	private var defaultImage: Drawable = getDrawable(R.drawable.file_image)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

	    initButtons()
    }

	private fun initButtons() {
		this.importButton.setOnClickListener { importAndSetImage() }
	}

	private fun importAndSetImage() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		if (intent.resolveActivity(packageManager) != null) {
			startActivityForResult(intent, 0)
		}
	}
	
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}
