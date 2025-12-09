package com.example.pr19_1_voroshilov

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: FloatingActionButton
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var dateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime(UUID.randomUUID())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as FloatingActionButton
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        dateText = view.findViewById(R.id.date_text) as TextView

        dateText.text = DateFormat.getDateInstance(DateFormat.LONG).format(crime.date)

        dateButton.apply {
            isEnabled = false
        }

        dateButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment(crime))
                .commit()
        }
        solvedCheckBox.isEnabled = false
        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int,
                                           count: Int,
                                           after: Int) {
            }

            override fun onTextChanged(s: CharSequence?,
                                       start: Int,
                                       before: Int,
                                       count: Int) {
                crime.title = s.toString()
                solvedCheckBox.isEnabled = titleField.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->  crime.isSolved = isChecked
                dateButton.isEnabled = solvedCheckBox.isChecked
            }
        }
    }
}