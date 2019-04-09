package com.genix.imagechanger.task3

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.genix.imagechanger.MainActivity
import com.genix.imagechanger.R
import kotlinx.android.synthetic.main.activity_draw_line.*


class DrawLineActivity : AppCompatActivity() {

	private var x1: Int = 0
	private var y1: Int = 0
	private var x2: Int = 0
	private var y2: Int = 0
	private var n: Int = 1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_draw_line)
		supportActionBar!!.hide()

		setImageView()
		initButtons()
		initEditTexts()
	}

	private fun initEditTexts() {
		x1EditText.setText("$x1")
		y1EditText.setText("$y1")
		x2EditText.setText("$x2")
		y2EditText.setText("$y2")
		nEditText.setText("$n")

		x1EditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val x = p0.toString().toInt()

					val width = MainActivity.currentImage!!.width
					if (x < 0 || x > (width - 1)) {
						toast("X must be in range of 0-${width - 1}")
						x1EditText.setText("0")
					} else {
						x1 = x
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		x2EditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val x = p0.toString().toInt()

					val width = MainActivity.currentImage!!.width
					if (x < 0 || x > (width - 1)) {
						toast("X must be in range of 0-${width - 1}")
						x2EditText.setText("0")
					} else {
						x2 = x
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		y1EditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val y = p0.toString().toInt()

					val height = MainActivity.currentImage!!.height
					if (y < 0 || y > (height - 1)) {
						toast("Y must be in range of 0-${height - 1}")
						y1EditText.setText("0")
					} else {
						y1 = y
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})

		y2EditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					val y = p0.toString().toInt()

					val height = MainActivity.currentImage!!.height
					if (y < 0 || y > (height - 1)) {
						toast("Y must be in range of 0-${height - 1}")
						y2EditText.setText("0")
					} else {
						y2 = y
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
	}

	private fun initButtons() {
		applyN1Button.setOnClickListener { midpointLine(x1, y1, x2, y2) }
	}

	private fun setImageView() {
		drawLineImageView.setImageBitmap(MainActivity.currentImage)
	}

	private fun midpointLine(x1: Int, y1: Int, x2: Int, y2: Int) {
		val dx = x2 - x1
		val dy = y2 - y1
		var d = 2 * dy - dx
		val dE = 2 * dy
		val dNE = 2 * (dy - dx)
		var x = x1
		var y = y1

		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888,true)

		putPixel(img, x, y)
		while (x < x2) {
			if (d < 0) { // move to E
				d += dE
				x++

			} else { // move to NE
				d += dNE
				++x
				++y
			}

			putPixel(img, x, y)
		}

		MainActivity.currentImage = img
		setImageView()
	}

	private fun putPixel(img: Bitmap, x: Int, y: Int) {
		img.setPixel(x, y, Color.BLACK)
	}


	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}

