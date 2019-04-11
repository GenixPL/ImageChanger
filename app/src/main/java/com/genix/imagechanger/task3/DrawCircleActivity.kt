package com.genix.imagechanger.task3

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import com.genix.imagechanger.MainActivity
import com.genix.imagechanger.R
import kotlinx.android.synthetic.main.activity_draw_circle.*
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt


class DrawCircleActivity : AppCompatActivity() {

	private var x: Int = (MainActivity.currentImage!!.width - 1) / 2
	private var y: Int = (MainActivity.currentImage!!.height - 1) / 2
	private var r: Int = 50
	private var n: Int = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_draw_circle)
		supportActionBar!!.hide()

		setImageView()
		initButtons()
		initEditTexts()
	}

	private fun initButtons() {
		applyButton.setOnClickListener { midpointCircle(x, y, r) }
		wuCircleButton.setOnClickListener { drawWuCircle(x, y, r) }
	}

	private fun initEditTexts() {
		xEditText.setText("$x")
		yEditText.setText("$y")
		rEditText.setText("$r")
		nEditText.setText("$n")

		xEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val temp = p0.toString().toInt()

					val width = MainActivity.currentImage!!.width
					if (temp < 0 || temp > (width - 1)) {
						toast("X must be in range of 0-${width - 1}")
						xEditText.setText("0")
					} else {
						x = temp
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		yEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val temp = p0.toString().toInt()

					val height = MainActivity.currentImage!!.height
					if (temp < 0 || temp > (height - 1)) {
						toast("Y must be in range of 0-${height - 1}")
						xEditText.setText("0")
					} else {
						y = temp
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		nEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					n = p0.toString().toInt()
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		rEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					r = p0.toString().toInt()
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})
	}

	private fun setImageView() {
		drawCircleImageView.setImageBitmap(MainActivity.currentImage)

		drawCircleImageView.setOnClickListener {
			val mBuilder = AlertDialog.Builder(this)
			val mView = layoutInflater.inflate(R.layout.dialog_zoomable_image, null)
			val photoView: ImageView = mView.findViewById(R.id.zoomableImageView)
			photoView.setImageBitmap(MainActivity.currentImage!!)
			mBuilder.setView(mView)
			val mDialog = mBuilder.create()
			mDialog.show()
		}
	}

	private fun midpointCircle(centX: Int, centY: Int, r: Int) {
		var d = 1 - r
		var x = 0
		var y = r

		val img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		putPixel(img, centX + x, centY + y)
		putPixel(img, centX + y, centY + x)
		putPixel(img, centX + y, centY - x)
		putPixel(img, centX + x, centY - y)
		putPixel(img, centX - x, centY - y)
		putPixel(img, centX - y, centY - x)
		putPixel(img, centX - y, centY + x)
		putPixel(img, centX - x, centY + y)

		while (y > x) {
			if (d < 0) { //move to E
				d += 2 * x + 3

			} else { //move to SE
				d += 2 * x - 2 * y + 5
				y--
			}

			x++

			putPixel(img, centX + x, centY + y)
			putPixel(img, centX + y, centY + x)
			putPixel(img, centX + y, centY - x)
			putPixel(img, centX + x, centY - y)
			putPixel(img, centX - x, centY - y)
			putPixel(img, centX - y, centY - x)
			putPixel(img, centX - y, centY + x)
			putPixel(img, centX - x, centY + y)
		}

		MainActivity.currentImage = img
		setImageView()
	}

	private fun drawWuCircle(centX: Int, centY: Int, r: Int) {
		val img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		val l = Color.BLACK /*Line color*/
		val B = img.getPixel(centX, centY) /*Background Color*/
		var x = r
		var y = 0

		putPixel(img, centX + x, centY + y, l)
		putPixel(img, centX + y, centY + x, l)
		putPixel(img, centX + y, centY - x, l)
		putPixel(img, centX + x, centY - y, l)
		putPixel(img, centX - x, centY - y, l)
		putPixel(img, centX - y, centY - x, l)
		putPixel(img, centX - y, centY + x, l)
		putPixel(img, centX - x, centY + y, l)

		while (x > y) {
			++y
			x = ceil(sqrt((r * r - y * y).toDouble())).toInt()
			val T: Float = ceil(sqrt(r.toFloat().pow(2) - y.toFloat().pow(2))) -
					sqrt(r.toFloat().pow(2) - y.toFloat().pow(2))
			val c2 = (l * (1 - T) + B * T).toInt()
			val c1 = (l * T + B * (1 - T)).toInt()

			putPixel(img, centX + x, centY + y, c2)
			putPixel(img, centX + x - 1, centY + y, c1)

			putPixel(img, centX + y, centY + x, c2)
			putPixel(img, centX + y - 1, centY + x, c1)

			putPixel(img, centX + y, centY - x, c2)
			putPixel(img, centX + y - 1, centY - x, c1)

			putPixel(img, centX + x, centY - y, c2)
			putPixel(img, centX + x - 1, centY - y, c1)

			putPixel(img, centX - x, centY - y, c2)
			putPixel(img, centX - x + 1, centY - y, c1)

			putPixel(img, centX - y, centY - x, c2)
			putPixel(img, centX - y + 1, centY - x, c1)

			putPixel(img, centX - y, centY + x, c2)
			putPixel(img, centX - y + 1, centY + x, c1)

			putPixel(img, centX - x, centY + y, c2)
			putPixel(img, centX - x + 1, centY + y, c1)
		}

		MainActivity.currentImage = img
		setImageView()
	}

	private fun putPixel(img: Bitmap, x: Int, y: Int, color: Int) {
		val height = MainActivity.currentImage!!.height - 1
		val width = MainActivity.currentImage!!.width - 1

		if (x < 0 || x > width || y < 0 || y > height) {
			return
		}

		img.setPixel(x, y, color)
	}

	private fun putPixel(img: Bitmap, x: Int, y: Int) {
		val height = MainActivity.currentImage!!.height - 1
		val width = MainActivity.currentImage!!.width - 1

		val lowerX = x - n
		val upperX = x - n + (n * 2)
		val lowerY = y - n
		val upperY = y - n + (n * 2)

		val r: Double = n + 0.5

		for (i in lowerX..upperX) {
			for (j in lowerY..upperY) {
				if (i < 0 || i > width || j < 0 || j > height) {
					continue
				}

				val curX: Double = i - r - (x - r)
				val curY: Double = j - r - (y - r)

				if (curX.pow(2) + curY.pow(2) <= r.pow(2)) {
					img.setPixel(i, j, Color.BLACK)
				}
			}
		}
	}


	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}
