package com.example.travelmemory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class AddTravelActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var etPlace: EditText
    private lateinit var etDate: EditText
    private lateinit var etMemo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_travel)
        supportActionBar?.title = "여행 기록 추가"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DBHelper(this)
        etPlace = findViewById(R.id.etPlace)
        etDate = findViewById(R.id.etDate)
        etMemo = findViewById(R.id.etMemo)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            // 커밋 2에서 구현
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}