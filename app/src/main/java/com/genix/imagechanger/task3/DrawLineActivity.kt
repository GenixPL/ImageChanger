package com.genix.imagechanger.task3

import android.graphics.*
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.Toast
import com.genix.imagechanger.MainActivity
import com.genix.imagechanger.R
import kotlinx.android.synthetic.main.activity_draw_line.*
import kotlin.math.abs
import kotlin.math.pow


class DrawLineActivity : AppCompatActivity() {

	private var x1: Int = 50
	private var y1: Int = 50
	private var x2: Int = 200
	private var y2: Int = 200
	private var n: Int = 0

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
		applyButton.setOnClickListener { drawLine() }
		labButton.setOnClickListener { lab() }
		wuLineButton.setOnClickListener { drawAliasedLine() }
	}

	private fun setImageView() {
		drawLineImageView.setImageBitmap(MainActivity.currentImage)

		drawLineImageView.setOnClickListener {
			val mBuilder = AlertDialog.Builder(this)
			val mView = layoutInflater.inflate(R.layout.dialog_zoomable_image, null)
			val photoView: ImageView = mView.findViewById(R.id.zoomableImageView)
			photoView.setImageBitmap(MainActivity.currentImage!!)
			mBuilder.setView(mView)
			val mDialog = mBuilder.create()
			mDialog.show()
		}
	}

	private fun drawLine() {
		val right = MainActivity.currentImage!!.width - 1
		val bottom = MainActivity.currentImage!!.height - 1

		if (x1 == x2) {
			drawVerticalLine(x1, y1, x2, y2)
			return
		}

		if (y1 == y2) {
			drawHorizontalLine(x1, y1, x2, y2)
			return
		}

		if (abs((x2 - x1)) < abs((y2 - y1))) {
			toast("difference in xs should be bigger or equal than in ys, because midpoint won't work otherwise")
			return
		}

		if (x1 < x2) {
			if (y1 < y2) {
				midpointLine(x1, y1, x2, y2)
				return
			} else {
				midpointLineMirrorTopBottom(x1, bottom - y1, x2, bottom - y2)
				return
			}
		}

		if (x1 > x2) {
			if (y1 < y2) {
				midpointLineMirrorLeftRight(right - x1, y1, right - x2, y2)
				return
			} else {
				midpointLine(x2, y2, x1, y1)
				return
			}
		}
	}

	private fun drawVerticalLine(x1: Int, y1: Int, x2: Int, y2: Int) {
		var smaller: Int
		var bigger: Int
		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		if (y1 < y2) {
			smaller = y1
			bigger = y2
		} else {
			smaller = y2
			bigger = y1
		}

		while (smaller < bigger) {
			putPixel(img, x1, smaller)
			smaller++
		}

		MainActivity.currentImage = img
		setImageView()
	}

	private fun drawHorizontalLine(x1: Int, y1: Int, x2: Int, y2: Int) {
		var smaller: Int
		var bigger: Int
		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		if (x1 < x2) {
			smaller = x1
			bigger = x2
		} else {
			smaller = x2
			bigger = x1
		}

		while (smaller < bigger) {
			putPixel(img, smaller, y1)
			smaller++
		}

		MainActivity.currentImage = img
		setImageView()
	}

	private fun midpointLine(x1: Int, y1: Int, x2: Int, y2: Int) {
		var dx = x2 - x1
		var dy = y2 - y1
		var d = 2 * dy - dx // initial value of d
		var dE = 2 * dy // increment used when moving to E
		var dNE = 2 * (dy - dx) // increment used when moving to NE
		var x = x1
		var y = y1

		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

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

	private fun midpointLineMirrorLeftRight(x1: Int, y1: Int, x2: Int, y2: Int) {
		var dx = x2 - x1
		var dy = y2 - y1
		var d = 2 * dy - dx // initial value of d
		var dE = 2 * dy // increment used when moving to E
		var dNE = 2 * (dy - dx) // increment used when moving to NE
		var x = x1
		var y = y1

		val right = MainActivity.currentImage!!.width - 1
		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		putPixel(img, right - x, y)
		while (x < x2) {
			if (d < 0) { // move to E
				d += dE
				x++

			} else { // move to NE
				d += dNE
				++x
				++y
			}
			putPixel(img, right - x, y)
		}


		MainActivity.currentImage = img
		setImageView()
	}

	private fun midpointLineMirrorTopBottom(x1: Int, y1: Int, x2: Int, y2: Int) {
		var dx = x2 - x1
		var dy = y2 - y1
		var d = 2 * dy - dx // initial value of d
		var dE = 2 * dy // increment used when moving to E
		var dNE = 2 * (dy - dx) // increment used when moving to NE
		var x = x1
		var y = y1

		val bottom = MainActivity.currentImage!!.height - 1
		var img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		putPixel(img, x, bottom - y)
		while (x < x2) {
			if (d < 0) { // move to E
				d += dE
				x++

			} else { // move to NE
				d += dNE
				++x
				++y
			}
			putPixel(img, x, bottom - y)
		}


		MainActivity.currentImage = img
		setImageView()
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

	private fun drawAliasedLine() {

		val img: Bitmap = MainActivity.currentImage!!.copy(Bitmap.Config.ARGB_8888, true)

		drawWuLine(img, x1, y1, x2, y2)
		MainActivity.currentImage = img
		setImageView()
	}


	/* === LAB === */
	private fun lab() {
		val m = 4

		val matrix = ColorMatrix()
		matrix.setSaturation(0f)

		val filter = ColorMatrixColorFilter(matrix)
		MainActivity.currentImage = toGrayScale(MainActivity.currentImage!!)

		MainActivity.currentImage = getSuperBitmap(MainActivity.currentImage!!, m)
		n *= m
		x1 *= m
		y1 *= m
		x2 *= m
		y2 *= m
		drawLine()
		n /= m
		x1 /= m
		y1 /= m
		x2 /= m
		y2 /= m
		MainActivity.currentImage = getShrankBitmap(MainActivity.currentImage!!, m)

		setImageView()
	}

	private fun toGrayScale(bmpOriginal: Bitmap): Bitmap {
		val width: Int
		val height: Int
		height = bmpOriginal.height
		width = bmpOriginal.width

		val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
		val c = Canvas(bmpGrayscale)
		val paint = Paint()
		val cm = ColorMatrix()
		cm.setSaturation(0f)
		val f = ColorMatrixColorFilter(cm)
		paint.setColorFilter(f)
		c.drawBitmap(bmpOriginal, 0f, 0f, paint)
		return bmpGrayscale
	}

	private fun getShrankBitmap(bmp: Bitmap, m: Int): Bitmap {
		val width = bmp.width / m
		val height = bmp.height / m

		val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

		for (i in 0 until (width)) {
			for (j in 0 until (height)) {
				val x = i * m
				val y = j * m

				var col = 0
				for (q in 0 until m) {
					for (w in 0 until m) {
						col += getGray(bmp.getPixel(x + q, y + w))
					}
				}

				col /= (m * m)

				bitmap.setPixel(i, j, Color.rgb(col, col, col))
			}
		}

		return bitmap
	}

	private fun getGray(pix: Int): Int {
		val red = Color.red(pix)
		val green = Color.green(pix)
		val blue = Color.blue(pix)

		return ((0.3 * red) + (0.59 * green) + (0.11 * blue)).toInt()
	}

	private fun getSuperBitmap(bmp: Bitmap, m: Int): Bitmap {
		val width = bmp.width
		val height = bmp.height

		val bitmap = Bitmap.createBitmap(width * m, height * m, Bitmap.Config.ARGB_8888)

		for (i in 0 until (width)) {
			for (j in 0 until (height)) {
				val x = i * m
				val y = j * m
				val pix = bmp.getPixel(i, j)

				for (q in 0 until m) {
					for (w in 0 until m) {
						bitmap.setPixel(x + q, y + w, pix)
					}
				}
			}
		}

		return bitmap
	}


	/* === OTHER === */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}

