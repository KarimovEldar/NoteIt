package com.example.todo.data.repository

import androidx.lifecycle.LiveData
import com.example.todo.data.ToDoDao
import com.example.todo.data.models.ToDoData

class ToDoRepository(private val toDoDao: ToDoDao) {

    val getAllData:LiveData<List<ToDoData>> = toDoDao.getAllData()
    val sortByHighPriority:LiveData<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortByLowPriority:LiveData<List<ToDoData>> = toDoDao.sortByLowPriority()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData){
        toDoDao.deleteData(toDoData)
    }

    suspend fun deleteAllData(){
        toDoDao.deleteAll()
    }

    fun searchDatabase(searchQuery:String):LiveData<List<ToDoData>>{
        return toDoDao.searchDatabase(searchQuery)
    }

}