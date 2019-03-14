package com.genix.imagechanger

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.Type
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_ordered_dithering.*

class OrderedDitheringActivity : AppCompatActivity() {

	private var rsContext : RenderScript? = null
	private var k = 2
	private var n = 2
	private var matrix: FloatArray = floatArrayOf(1f, 3f, 4f, 2f)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_ordered_dithering)
		supportActionBar!!.hide()

		setImageView()
		initButtons()
		initSpinners()
		setMatrix()

		rsContext = RenderScript.create(this)
	}

	private fun setImageView() {
		imageView.setImageBitmap(MainActivity.currentImage)
	}

	private fun initButtons() {
		applyButton.setOnClickListener { BackgroundOrderedDithering().execute() }
	}

	private fun initSpinners() {
		val nValues = arrayOf(2, 3, 4, 8)
		val kValues = arrayOf(2, 4, 8, 16, 32, 64)

		nSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nValues)
		kSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kValues)

		kSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
			override fun onNothingSelected(parent: AdapterView<*>?) { }

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				k = kValues[position]
//				Log.d("DEBUG", "k = $k")
			}
		}

		nSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
			override fun onNothingSelected(parent: AdapterView<*>?) { }

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				n = nValues[position]
//				Log.d("DEBUG", "n = $n")

				when (n) {
					2 -> matrix = floatArrayOf(0f, 2f, 3f, 1f)
					3 -> matrix = floatArrayOf(0f, 7f, 3f, 6f, 5f, 2f, 4f, 1f, 8f)
					4 -> matrix = floatArrayOf(0f, 8f, 2f, 10f, 12f, 4f, 14f, 6f, 3f, 11f, 1f, 9f, 15f, 7f, 13f, 5f)
					8 -> matrix = floatArrayOf(
						0f, 48f, 12f, 60f, 3f, 51f, 15f, 63f,
						32f, 16f, 44f, 28f, 35f, 19f, 47f, 31f,
						8f, 56f, 4f, 52f, 11f, 59f, 7f, 55f,
						40f, 24f, 36f, 20f, 43f, 27f, 39f, 23f,
						2f, 50f, 14f, 62f, 1f, 49f, 13f, 61f,
						34f, 18f, 46f, 30f, 33f, 17f, 45f, 29f,
						10f, 58f, 6f, 54f, 9f, 57f, 5f, 53f,
						42f, 26f, 38f, 22f, 41f, 25f, 37f, 21f
						)
					else -> toast("Wrong number of n")
				}

				setMatrix()
			}
		}
	}

	private fun setMatrix() {
		matrixLayout.removeAllViews()

		var i = 0
		while (i < n) {
			var horizontalLayout = LinearLayout(this)
			horizontalLayout.gravity = Gravity.HORIZONTAL_GRAVITY_MASK
			horizontalLayout.layoutParams = LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				1f
			)

			var j = 0
			while (j < n) {
				var editText = EditText(this)
				editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
				editText.layoutParams = LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT,
					1f
				)
				editText.setText(matrix[i * n + j].toString())
				editText.gravity = Gravity.CENTER
				editText.tag = i * n + j

				editText.addTextChangedListener(object : TextWatcher {
					override fun afterTextChanged(p0: Editable?) {
						if (p0.toString().isNotEmpty() && p0.toString() != "-") {
							matrix[editText.tag.toString().toInt()] = p0.toString().toFloat()
							Log.d("TAG", "matrix:${editText.tag.toString().toInt()} = ${matrix[editText.tag.toString().toInt()]}")
						}
					}

					override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
					override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
				})

				horizontalLayout.addView(editText)

				j++
			}

			matrixLayout.addView(horizontalLayout)

			i++
		}
	}



	inner class BackgroundOrderedDithering : AsyncTask<Void, Void, Bitmap>() {

		override fun doInBackground(vararg params: Void?): Bitmap {
			var bitmap: Bitmap = Bitmap.createBitmap(
				MainActivity.currentImage!!.width,
				MainActivity.currentImage!!.height,
				Bitmap.Config.ARGB_8888
			)
			Log.d("DEBUG", "width: ${MainActivity.currentImage!!.width} height: ${MainActivity.currentImage!!.height}")

			var aIn = Allocation.createFromBitmap(rsContext, MainActivity.currentImage)
			var aOut = Allocation.createFromBitmap(rsContext, bitmap)
			val odScript = ScriptC_ordered_dithering(rsContext)
			odScript._k = k
			odScript._n = n

			var matrixCopy = matrix
			for (i in 0..(matrix.size - 1)) {
				matrixCopy[i] = matrix[i] / (n * n)
			}

			var matrixBuilder = Type.Builder(rsContext, Element.F32(rsContext))
			matrixBuilder.setX(matrix.size)
			var matrixAlloc = Allocation.createTyped(rsContext, matrixBuilder.create())
			matrixAlloc.copyFrom(matrixCopy)
			odScript.bind_matrix(matrixAlloc)
			Log.d("DEBUG: ", matrixCopy.contentToString())

			odScript.forEach_ordered_dithering(aIn, aOut)
			aOut.copyTo(bitmap)

			return bitmap
		}

		override fun onPostExecute(result: Bitmap?) {
			toast("Ordered dithering is done")
			MainActivity.currentImage = result
			setImageView()
		}
	}



	/* OTHER METHODS */
	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}

}
