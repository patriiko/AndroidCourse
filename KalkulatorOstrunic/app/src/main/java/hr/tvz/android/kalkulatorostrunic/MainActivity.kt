package hr.tvz.android.kalkulatorostrunic

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import hr.tvz.android.kalkulatorostrunic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Spinner za promjenu teme
        val modes = listOf(
            getString(R.string.light_mode),
            getString(R.string.dark_mode),
            getString(R.string.system_mode)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modes)
        binding.fontSizeSpinner.adapter = adapter

        binding.fontSizeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.calculateButton.setOnClickListener {
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        val weight = binding.weightInput.text.toString().toFloatOrNull()
        val height = binding.heightInput.text.toString().toFloatOrNull()

        if (weight == null || height == null || height == 0f) {
            Toast.makeText(this, getString(R.string.input_error), Toast.LENGTH_SHORT).show()
            return
        }

        val heightM = height / 100
        val bmi = weight / (heightM * heightM)

        val category = when {
            bmi < 18.5 -> getString(R.string.underweight)
            bmi < 25 -> getString(R.string.normal)
            bmi < 30 -> getString(R.string.overweight)
            bmi < 40 -> getString(R.string.obese)
            else -> getString(R.string.severely_obese)
        }

        val result = String.format(getString(R.string.bmi_result), bmi, category)
        binding.resultText.text = result

        binding.weightInput.text?.clear()
        binding.heightInput.text?.clear()
    }
}
