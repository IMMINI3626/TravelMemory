package com.example.travelmemory

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var recordId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.title = "여행 상세"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DBHelper(this)
        recordId = intent.getLongExtra("record_id", -1)

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, EditTravelActivity::class.java)
            intent.putExtra("record_id", recordId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            showDeleteDialog()
        }

        loadRecord()
    }

    override fun onResume() {
        super.onResume()
        loadRecord()
    }

    private fun loadRecord() {
        val record = dbHelper.getTravelById(recordId) ?: run { finish(); return }

        findViewById<TextView>(R.id.tvPlace).text = record.place
        findViewById<TextView>(R.id.tvDate).text = record.visitDate
        findViewById<TextView>(R.id.tvMemo).text = record.memo.ifEmpty { "메모 없음" }

        val imgPhoto = findViewById<ImageView>(R.id.imgPhoto)
        if (!record.photoUri.isNullOrEmpty()) {
            try {
                imgPhoto.setImageURI(Uri.parse(record.photoUri))
            } catch (e: Exception) {
                imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        } else {
            imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("삭제 확인")
            .setMessage("이 여행 기록을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                dbHelper.deleteTravel(recordId)
                finish()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}