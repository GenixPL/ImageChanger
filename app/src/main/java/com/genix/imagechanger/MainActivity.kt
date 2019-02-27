package com.genix.imagechanger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast


class MainActivity : AppCompatActivity() {

//	val importButton = findViewById<Button>(R.id.importButton)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()

	    initButtons()
    }

	private fun initButtons() {
//		val clickListener = View.OnClickListener {view ->
//			toast("Aaa")
//		}
//
//		importButton.setOnClickListener(clickListener)

//		class Holder(val value: Int)
//		val a = Holder(3)
//		val b = Holder(3)
//		Toast.makeText(this, (a == b).toString(), Toast.LENGTH_SHORT).show()


		fun String.shorten() {
			return this + “...”
		}
	}

	private fun toast(message: String) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
	}
}
