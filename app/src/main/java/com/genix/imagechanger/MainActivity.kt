package com.genix.imagechanger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


const val IMAGE_PICK_CODE = 10
const val WRITE_REQUEST_CODE = 20

class MainActivity : AppCompatActivity() {

	private var defaultImage: Bitmap? = null

	companion object {
		var currentImage: Bitmap? = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		supportActionBar!!.hide()

		initButtons()
	}

	override fun onResume() {
		super.onResume()
		setImage()
	}

	private fun initButtons() {
		importButton.setOnClickListener { importImage() }

		saveButton.setOnClickListener { saveImageToInternalStorage() }

		setDefaultButton.setOnClickListener { setCurrentImageAsDefault() }

		importDefaultButton.setOnClickListener { setDefaultAsCurrent() }

		functionalFiltersButton.setOnClickListener { moveToFunctionalFilters() }
	}

	private fun importImage() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"

		if (intent.resolveActivity(packageManager) != null) {
			startActivityForResult(intent, IMAGE_PICK_CODE)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {

			var imageUri: Uri? = data?.data
			currentImage = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

			setImage()
		}

		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun setImage() {
		imageView.setImageBitmap(currentImage)
	}

	private fun setCurrentImageAsDefault() {
		if (currentImage == null) {
			toast("There is no image currently")
		} else {
			defaultImage = currentImage
		}
	}

	private fun setDefaultAsCurrent() {
		if (defaultImage == null) {
			toast("There is no default image")
		} else {
			currentImage = defaultImage
			setImage()
		}
	}

	private fun saveImageToInternalStorage() {
		ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_REQUEST_CODE)

		if (currentImage == null) {
			toast("There is no image to save")
			return
		}

		val root = Environment.getExternalStorageDirectory().toString()
		val myDir = File("$root/saved_photos")
		myDir.mkdirs()

		val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
		var file = File(myDir, "$timeStamp.jpg")


		try {
			val stream: OutputStream = FileOutputStream(file)

			currentImage?.compress(Bitmap.CompressFormat.JPEG, 100, stream)

			stream.flush()
			stream.close()

		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	private fun moveToFunctionalFilters() {
		if (currentImage == null) {
			toast("There is no image currently")

		} else {
			val intent = Intent(this, FunctionalFiltersActivity::class.java)
			startActivity(intent)
		}
	}


	/* OTHER METHODS */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}
