package com.genix.imagechanger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_edit_custom_matrix.*
import kotlin.math.floor

class EditCustomMatrixActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_edit_custom_matrix)
		supportActionBar!!.hide()

		setDefaultData()
		initSpinners()
		initEditTexts()
		setMatrix()
	}

	private fun setDefaultData() {
		ConvolutionFiltersActivity.matrixWidth = 3
		ConvolutionFiltersActivity.matrixHeight = 3
		ConvolutionFiltersActivity.offset = 0
		ConvolutionFiltersActivity.divisor = 1.0f
		ConvolutionFiltersActivity.changesInCoordinatesToConsider = intArrayOf(-1, -1, 0, -1, 1, -1, -1, 0, 0, 0, 1, 0, -1, 1, 0, 1, 1, 1)
		ConvolutionFiltersActivity.matrix = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
	}

	private fun initSpinners() {
		val spinnerValues = arrayOf(3, 5, 7, 9)

		widthSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerValues)
		heightSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerValues)

		widthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
			override fun onNothingSelected(parent: AdapterView<*>?) { }

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				ConvolutionFiltersActivity.matrixWidth = ((position + 1) * 2) + 1
				ConvolutionFiltersActivity.matrix = FloatArray(ConvolutionFiltersActivity.matrixWidth * ConvolutionFiltersActivity.matrixHeight) { 1f }
				setAnchor(anchorEditText.text.toString().toInt())
			}
		}

		heightSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
			override fun onNothingSelected(parent: AdapterView<*>?) { }

			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				ConvolutionFiltersActivity.matrixHeight = ((position + 1) * 2) + 1
				ConvolutionFiltersActivity.matrix = FloatArray(ConvolutionFiltersActivity.matrixWidth * ConvolutionFiltersActivity.matrixHeight) { 1f }
				setAnchor(anchorEditText.text.toString().toInt())
			}
		}
	}

	private fun initEditTexts() {
		offsetEditText.setText("0")
		divisorEditText.setText("1.0")
		anchorEditText.setText("4")

		offsetEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty() && p0.toString() != "-") {
					ConvolutionFiltersActivity.offset = p0.toString().toInt()
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
		})

		divisorEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					ConvolutionFiltersActivity.divisor = p0.toString().toFloat()
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
		})

		anchorEditText.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {
				if (p0.toString().isNotEmpty()) {
					var anchor = p0.toString().toInt()

					var width = ConvolutionFiltersActivity.matrixWidth
					var height = ConvolutionFiltersActivity.matrixHeight
					Log.d("TAG", "w:$width h:$height")
					if (anchor < 0 || anchor > ((width * height) - 1)) {
						toast("Anchor must be in range of 0-${(width * height) - 1}")
						anchorEditText.setText("0")
					} else {
						setAnchor(anchor)
					}
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
		})
	}

	private fun setAnchor(anchor: Int) {
		val width = ConvolutionFiltersActivity.matrixWidth.toFloat()

		//calculate anchor's row and column
		var aRow = floor(anchor / width).toInt()
		var aCol = (anchor - (floor(anchor / width) * width)).toInt()
		//Log.d("TAG", "row:$aRow, col:$aCol")

		var changesInCoordinates = IntArray(ConvolutionFiltersActivity.matrixWidth * ConvolutionFiltersActivity.matrixHeight * 2)

		var i = 0
		while (i < ConvolutionFiltersActivity.matrixWidth * ConvolutionFiltersActivity.matrixHeight) {
			//calculate current row and column
			var cRow = floor(i / width).toInt()
			var cCol = (i - (floor(i / width) * width)).toInt()

			//set differences in array
			changesInCoordinates[i * 2] = cRow - aRow
			changesInCoordinates[(i * 2) + 1] = cCol - aCol

			//Log.d("TAG", "changes:$i, row:${changesInCoordinates[i * 2]}, col:${changesInCoordinates[(i * 2) + 1]}")

			i++
		}

		ConvolutionFiltersActivity.changesInCoordinatesToConsider = changesInCoordinates
	}

	private fun setMatrix() {
		
	}


	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}