package com.example.travelmemory

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class TravelListFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: TravelAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_travel_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = DBHelper(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        adapter = TravelAdapter(
            mutableListOf(),
            onItemClick = { record ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("record_id", record.id)
                startActivity(intent)
            },
            onEditClick = { record ->
                val intent = Intent(requireContext(), EditTravelActivity::class.java)
                intent.putExtra("record_id", record.id)
                startActivity(intent)
            },
            onDeleteClick = { record -> showDeleteDialog(record) }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        registerForContextMenu(recyclerView)

        loadAll()
    }

    override fun onResume() {
        super.onResume()
        loadAll()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return adapter.handleContextMenu(item.itemId) || super.onContextItemSelected(item)
    }

    private fun loadAll() {
        adapter.updateList(dbHelper.getAllTravels())
    }

    private fun showDeleteDialog(record: TravelRecord) {
        AlertDialog.Builder(requireContext())
            .setTitle("삭제 확인")
            .setMessage("'${record.place}' 여행 기록을 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                dbHelper.deleteTravel(record.id)
                loadAll()
            }
            .setNegativeButton("취소", null)
            .show()
    }
}