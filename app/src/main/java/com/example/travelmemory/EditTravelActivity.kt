package com.example.travelmemory

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.util.Calendar

class EditTravelActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var etPlace: EditText
    private lateinit var etDate: EditText
    private lateinit var etMemo: EditText
    private lateinit var imgPreview: ImageView
    private var selectedPhotoUri: Uri? = null
    private var cameraImageUri: Uri? = null
    private var extractedLat: Double? = null
    private var extractedLng: Double? = null
    private var recordId: Long = -1

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPhotoUri = uri
                imgPreview.setImageURI(uri)
                extractGps(uri)
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cameraImageUri?.let { uri ->
                selectedPhotoUri = uri
                imgPreview.setImageURI(uri)
                extractGps(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_travel)
        supportActionBar?.title = "여행 기록 수정"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DBHelper(this)
        etPlace = findViewById(R.id.etPlace)
        etDate = findViewById(R.id.etDate)
        etMemo = findViewById(R.id.etMemo)
        imgPreview = findViewById(R.id.imgPreview)

        recordId = intent.getLongExtra("record_id", -1)

        etDate.setOnClickListener { showDatePicker() }
        findViewById<Button>(R.id.btnCamera).setOnClickListener { openCamera() }
        findViewById<Button>(R.id.btnGallery).setOnClickListener { openGallery() }
        findViewById<Button>(R.id.btnSave).setOnClickListener { saveRecord() }
        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }

        loadRecord()
    }

    private fun loadRecord() {
        val record = dbHelper.getTravelById(recordId) ?: run { finish(); return }
        etPlace.setText(record.place)
        etDate.setText(record.visitDate)
        etMemo.setText(record.memo)
        extractedLat = record.latitude
        extractedLng = record.longitude
        record.photoUri?.let {
            selectedPhotoUri = Uri.parse(it)
            imgPreview.setImageURI(selectedPhotoUri)
        }
    }

    private fun extractGps(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val latLong = FloatArray(2)
                if (exif.getLatLong(latLong)) {
                    extractedLat = latLong[0].toDouble()
                    extractedLng = latLong[1].toDouble()
                    Toast.makeText(this, "위치 정보 추출 완료!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun openCamera() {
        try {
            val photoFile = File.createTempFile("travel_edit_", ".jpg", cacheDir)
            cameraImageUri = FileProvider.getUriForFile(
                this, "${packageName}.provider", photoFile
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "카메라를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
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
            memo = memo,
            photoUri = selectedPhotoUri?.toString(),
            latitude = extractedLat,
            longitude = extractedLng
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