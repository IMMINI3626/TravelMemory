package com.example.travelmemory

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class EditTravelActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var etPlace: EditText
    private lateinit var etDate: EditText
    private lateinit var etMemo: EditText
    private var recordId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_travel)
        supportActionBar?.title = "여행 기록 수정"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DBHelper(this)
        etPlace = findViewById(R.id.etPlace)
        etDate = findViewById(R.id.etDate)
        etMemo = findViewById(R.id.etMemo)

        recordId = intent.getLongExtra("record_id", -1)

        etDate.setOnClickListener { showDatePicker() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveRecord() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        loadRecord()
    }

    private fun loadRecord() {
        val record = dbHelper.getTravelById(recordId) ?: run { finish(); return }
        etPlace.setText(record.place)
        etDate.setText(record.visitDate)
        etMemo.setText(record.memo)
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun saveRecord() {
        val place = etPlace.text.toString().trim()
        val date = etDate.text.toString().trim()
        val memo = etMemo.text.toString().trim()

        if (place.isEmpty()) { etPlace.error = "여행지명을 입력하세요"; return }
        if (date.isEmpty()) { etDate.error = "날짜를 선택하세요"; return }

        val record = TravelRecord(
            id = recordId,
            place = place,
            visitDate = date,
            memo = memo
        )
        dbHelper.updateTravel(record)
        Toast.makeText(this, "수정되었습니다!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}