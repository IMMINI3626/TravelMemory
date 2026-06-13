package com.example.travelmemory

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TravelListFragment : Fragment() {

    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: TravelAdapter
    private var currentOrderBy = "visit_date DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        val searchView = view.findViewById<SearchView>(R.id.searchView)

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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val keyword = newText?.trim() ?: ""
                if (keyword.isEmpty()) loadAll()
                else adapter.updateList(dbHelper.searchTravel(keyword))
                return true
            }
        })

        loadAll()

        val btnMenu = view.findViewById<android.widget.ImageButton>(R.id.btnMenu)
        btnMenu.setOnClickListener { view ->
            val popup = android.widget.PopupMenu(requireContext(), view)
            popup.menuInflater.inflate(R.menu.menu_travel_list, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_sort_newest -> { currentOrderBy = "visit_date DESC"; loadAll(); true }
                    R.id.menu_sort_oldest -> { currentOrderBy = "visit_date ASC"; loadAll(); true }
                    R.id.menu_sort_name -> { currentOrderBy = "place ASC"; loadAll(); true }
                    R.id.menu_app_info -> {
                        AlertDialog.Builder(requireContext())
                            .setTitle("앱 정보")
                            .setMessage("Travel Memory\n버전 1.0\n\n순천향대학교 모바일프로그래밍 기말 프로젝트")
                            .setPositiveButton("확인", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadAll()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return adapter.handleContextMenu(item.itemId) || super.onContextItemSelected(item)
    }

    private fun loadAll() {
        adapter.updateList(dbHelper.getAllTravels(currentOrderBy))
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