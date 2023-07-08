package com.todolist

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.todolist.adapters.ToDoAdapter
import com.todolist.databinding.ActivityMainBinding
import com.todolist.models.ToDoModel
import com.todolist.utils.DatabaseHandler
import java.util.*

class MainActivity : AppCompatActivity(), DialogCloseListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var taskList: ArrayList<ToDoModel>
    private lateinit var db: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        db = DatabaseHandler(this)
        db.openDatabase()
        tasksAdapter = ToDoAdapter(db, this@MainActivity)

        with(binding.tasksRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = tasksAdapter
        }

        val itemTouchHelper = ItemTouchHelper(RecyclerItemTouchHelper(tasksAdapter))
        itemTouchHelper.attachToRecyclerView(binding.tasksRecyclerView)

        taskList = db.getAllTasks()
        taskList.reverse()

        tasksAdapter.setTasks(taskList)

        binding.addTask.setOnClickListener {
            TaskDialog.newInstance().show(supportFragmentManager, TaskDialog.TAG)
        }
    }

    override fun handleDialogClose(dialog: DialogInterface?) {
        taskList = db.getAllTasks()
        taskList.reverse()
        tasksAdapter.setTasks(taskList)
        tasksAdapter.notifyDataSetChanged()
    }
}