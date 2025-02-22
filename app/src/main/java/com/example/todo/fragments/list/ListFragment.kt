package com.example.todo.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todo.R
import com.example.todo.data.models.ToDoData
import com.example.todo.data.viewmodel.ToDoViewModel
import com.example.todo.databinding.FragmentListBinding
import com.example.todo.fragments.SharedViewModel
import com.example.todo.fragments.list.adapter.ListAdapter
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator

class ListFragment : Fragment() , SearchView.OnQueryTextListener{
    private lateinit var binding : FragmentListBinding
    private val listAdapter : ListAdapter by lazy{
        ListAdapter()
    }
    private val toDoViewModel:ToDoViewModel by viewModels()
    private val sharedViewModel :SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater,container,false)

        setUpRecyclerView()

        toDoViewModel.getAllData.observe(viewLifecycleOwner, Observer {
            sharedViewModel.checkIfDatabaseEmpty(it)
            listAdapter.setData(it)
        })

        sharedViewModel.emptyDatabase.observe(viewLifecycleOwner){
            showEmptyDatabaseView(it)
        }

        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider{

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_list_menu,menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){

                    R.id.menu_delete_all -> deleteAllData()
                    R.id.menu_priority_high -> toDoViewModel.sortByHighPriority.observe(viewLifecycleOwner){listAdapter.setData(it)}
                    R.id.menu_priority_low -> toDoViewModel.sortByLowPriority.observe(viewLifecycleOwner){listAdapter.setData(it)}

                }
                return true
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchThroughDatabase(query)
        }
        return true
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchThroughDatabase(newText)
        }
        return true
    }

    private fun searchThroughDatabase(query: String){
        toDoViewModel.searchDatabase("%$query%").observe(viewLifecycleOwner){
            it.let {
                listAdapter.setData(it)
            }
        }
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.adapter = listAdapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.itemAnimator = SlideInLeftAnimator().apply {
            addDuration = 300
        }

        swipeToDelete(binding.recyclerView)
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = listAdapter.dataList[viewHolder.adapterPosition]
                // Delete Item
                toDoViewModel.deleteData(deletedItem)
                listAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(requireContext(),"Successfully Deleted!",Toast.LENGTH_SHORT).show()
                // Restore Deleted Item
                restoreDeletedItem(viewHolder.itemView,deletedItem,viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View,deletedItem:ToDoData,position : Int){

        val snackBar = Snackbar.make(view,
        "Deleted '${deletedItem.title}'",Snackbar.LENGTH_SHORT)
        snackBar.setAction("Undo"){
            toDoViewModel.insertData(deletedItem)

        }
        snackBar.show()


    }

    private fun deleteAllData(){

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            toDoViewModel.deleteAllData()
            Toast.makeText(requireContext(),"Successfully Deleted!",Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete all data")
        builder.setMessage("Are you sure you want to delete all data ?")
        builder.create().show()

    }

    private fun showEmptyDatabaseView(emptyDatabase:Boolean){
        if (emptyDatabase){
            binding.emptyTextView.visibility = View.VISIBLE
            binding.emptyImageView.visibility = View.VISIBLE
        }else{

            binding.emptyTextView.visibility = View.INVISIBLE
            binding.emptyImageView.visibility = View.INVISIBLE

        }
    }

}