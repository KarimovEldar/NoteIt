package com.example.todo.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.R
import com.example.todo.data.models.ToDoData
import com.example.todo.data.viewmodel.ToDoViewModel
import com.example.todo.databinding.FragmentUpdateBinding
import com.example.todo.fragments.SharedViewModel

class UpdateFragment : Fragment() {

    private lateinit var binding : FragmentUpdateBinding
    private val args by navArgs<UpdateFragmentArgs>()
    private val toDoViewModel : ToDoViewModel by viewModels()
    private val sharedViewModel : SharedViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdateBinding.inflate(inflater,container,false)

        binding.updateTitleEditText.setText(args.currentItem.title)
        binding.updateDescriptionEditText.setText(args.currentItem.description)
        binding.updatePrioritiesSpinner.setSelection(sharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.updatePrioritiesSpinner.onItemSelectedListener = sharedViewModel.listener

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_update_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_update -> updateData()
                    R.id.menu_delete -> deleteData()
                    android.R.id.home -> requireActivity().onBackPressed()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun updateData(){

        val title = binding.updateTitleEditText.text.toString()
        val description = binding.updateDescriptionEditText.text.toString()
        val priority = binding.updatePrioritiesSpinner.selectedItem.toString()

        if(sharedViewModel.inputCheck(title,priority)){
            val updatedData = ToDoData(
                args.currentItem.id,
                title,
                sharedViewModel.parsePriority(priority),
                description
            )

            toDoViewModel.updateData(updatedData)

            Toast.makeText(requireContext(),"Successfully updated!",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }else{
            Toast.makeText(requireContext(),"Please fill out all folders!",Toast.LENGTH_SHORT).show()
        }


    }

    fun deleteData(){

        val builder= AlertDialog.Builder(requireContext())

        builder.setPositiveButton("Yes"){_,_->

            toDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(),"Successfully Deleted!",Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }

        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete '${args.currentItem.title}'!")
        builder.setMessage("Are you sure you want to delete '${args.currentItem.title}'?")
        builder.create().show()
    }

}
