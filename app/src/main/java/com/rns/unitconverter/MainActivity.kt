package com.rns.unitconverter

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.rns.unitconverter.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.dateBtn.setOnClickListener(this)

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.magnitude_array,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.chooseMethodSp.adapter = adapter

        binding.numberEt.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(editable: Editable) {
                updateAndConvert()
            }
        })

        binding.unitFromSp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                updateAndConvert()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.unitToSp.onItemSelectedListener = binding.unitFromSp.onItemSelectedListener

        val magListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    var arrayID = 0
                    if (binding.chooseMethodSp.selectedItem.toString() == "Distance") {
                        arrayID = R.array.distance_array
                    } else if (binding.chooseMethodSp.selectedItem.toString() == "Weight") {
                        arrayID = R.array.weight_array
                    }
                    setValuesToSpinners(binding.unitFromSp.id, arrayID)
                    setValuesToSpinners(binding.unitToSp.id, arrayID)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        binding.chooseMethodSp.onItemSelectedListener = magListener
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.dateBtn -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data = Uri.parse("https://hijri-calendar.com/convert/")
                    startActivity(openURL)
                }
            }
        }
    }

    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Do you want to close this application ?")
            .setCancelable(false)
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                finish()
                super.onBackPressed()
            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Close App")
        alert.show()
    }

    private fun setValuesToSpinners(spinnerId: Int, arrayID: Int) {
        val spinner = findViewById<View>(spinnerId) as Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            arrayID,
            android.R.layout.simple_spinner_dropdown_item
        )
        spinner.adapter = adapter
    }

    private fun updateAndConvert() {
        if (binding.numberEt.editText?.text.toString() == "") {
            binding.numberEt.editText?.setText("0")
            binding.numberEt.editText?.setSelection(0, 1)
        }

        val baseValue: Float = if (binding.numberEt.editText?.text.toString() == ".") {
            0f
        } else {
            binding.numberEt.editText?.text.toString().toFloat()
        }
        var convFactor = 0.0
        var result = 0.0
        val convertFrom: String = binding.unitFromSp.getSelectedItem().toString()
        val convertTo: String = binding.unitToSp.getSelectedItem().toString()
        if (binding.chooseMethodSp.selectedItem.toString() == "Distance") {
            when (convertFrom) {
                "m" -> convFactor = 1.0
                "ft" -> convFactor = 0.3048
                "km" -> convFactor = 1000.0
            }
            result = baseValue * convFactor
            when (convertTo) {
                "m" -> convFactor = 1.0
                "ft" -> convFactor = 0.3048
                "km" -> convFactor = 1000.0
            }
            result /= convFactor
        } else if (binding.chooseMethodSp.selectedItem.toString() == "Weight") {
            when (convertFrom) {
                "g" -> convFactor = 1.0
                "kg" -> convFactor = 1000.0
            }
            result = baseValue * convFactor
            when (convertTo) {
                "g" -> convFactor = 1.0
                "kg" -> convFactor = 1000.0
            }
            result /= convFactor
        }

        binding.resultToTv.text = "%.2f".format(result)
    }
}