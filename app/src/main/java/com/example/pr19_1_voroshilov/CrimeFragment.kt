package com.example.pr19_1_voroshilov

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.text.format.DateFormat
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import java.util.UUID


private const val DATE_FORMAT = "EEE, MMM, dd"
private var REQUEST_CONTACT = 0
class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: FloatingActionButton
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var dateText: TextView
    private lateinit var sendCrimeBtn:AppCompatButton
    private lateinit var chooseSuspectBtn:AppCompatButton
    private lateinit var callBtn:AppCompatButton
    private var number:String = ""


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
        sendCrimeBtn = view.findViewById(R.id.send_report)
        sendCrimeBtn.setOnClickListener{
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,
                    getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->
                val chooserIntent =
                    Intent.createChooser(intent,
                        getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        chooseSuspectBtn = view.findViewById(R.id.choose_suspect)
        chooseSuspectBtn.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK,
                    ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }
        }

        callBtn = view.findViewById(R.id.call)
        callBtn.apply {
            setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL, "tel:${number}".toUri())
                startActivity(callIntent)
            }
        }


        dateText.text = DateFormat.getDateFormat(context).format(crime.date)

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

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved)
        {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved) }

        val dateString =
            DateFormat.format(DATE_FORMAT,
                crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report,
            crime.title, dateString,
            solvedString, suspect)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK ->
                return

            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)

                val cursor = requireActivity().contentResolver
                    .query(
                        contactUri!!,
                        queryFields,
                        null,
                        null,
                        null
                    )
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    chooseSuspectBtn.text = crime.suspect
                    val queryNumbers = arrayOf(CommonDataKinds.Phone.NUMBER)

                    val phoneCursor = requireActivity().contentResolver
                        .query(
                            CommonDataKinds.Phone.CONTENT_URI,
                            queryNumbers,
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(it.getString(1)),
                            null
                        )
                    phoneCursor?.use{
                        it.moveToFirst()
                        if (it.count == 0){
                            callBtn.isEnabled = false
                            return
                        }
                        number = it.getString(0)
                        if(number.isNotEmpty()) callBtn.isEnabled = true
                    }
                }
            }
        }
    }
}