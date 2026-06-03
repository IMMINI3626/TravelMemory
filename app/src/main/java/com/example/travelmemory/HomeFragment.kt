package com.example.travelmemory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DBHelper(requireContext())

        val tvCount = view.findViewById<TextView>(R.id.tvTotalCount)
        val tvRecentPlace = view.findViewById<TextView>(R.id.tvRecentPlace)
        val tvRecentDate = view.findViewById<TextView>(R.id.tvRecentDate)
        val btnAdd = view.findViewById<Button>(R.id.btnAddTravel)

        btnAdd.setOnClickListener {
            // 추후 AddTravelActivity 연결
        }

        updateStats(tvCount, tvRecentPlace, tvRecentDate)
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            updateStats(
                it.findViewById(R.id.tvTotalCount),
                it.findViewById(R.id.tvRecentPlace),
                it.findViewById(R.id.tvRecentDate)
            )
        }
    }

    private fun updateStats(tvCount: TextView, tvPlace: TextView, tvDate: TextView) {
        val records = dbHelper.getAllTravels()
        tvCount.text = "총 ${records.size}개의 여행 기록"
        if (records.isNotEmpty()) {
            tvPlace.text = records.first().place
            tvDate.text = records.first().visitDate
        } else {
            tvPlace.text = "아직 기록이 없어요"
            tvDate.text = ""
        }
    }
}