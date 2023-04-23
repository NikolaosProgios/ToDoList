package com.todolist.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.todolist.AddNewTask
import com.todolist.MainActivity
import com.todolist.R
import com.todolist.TaskDialog
import com.todolist.models.ToDoModel
import com.todolist.utils.DatabaseHandler

class ToDoAdapter(
    private val db: DatabaseHandler,
    private val activity: MainActivity
) : RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {

    private lateinit var todoList: ArrayList<ToDoModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getContext(): Context {
        return activity
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.openDatabase()

        val item = todoList[position]
        holder.taskText.text = item.text
        holder.taskCheckBox.isChecked = toBoolean(item.status!!)
        holder.taskCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id!!, 1)
            } else {
                db.updateStatus(item.id!!, 0)
            }
        }
    }

    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    fun setTasks(todoList: ArrayList<ToDoModel>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = todoList[position]
        db.deleteTask(item.id!!)
        todoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList[position]
        val bundle = Bundle()
        bundle.putInt("id", item.id!!)
        bundle.putString("task", item.text)
//        val fragment = AddNewTask()
        val fragment = TaskDialog()
        fragment.arguments = bundle
//        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
        fragment.show(activity.supportFragmentManager, TaskDialog.TAG)
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var taskCheckBox: CheckBox
        var taskText: TextView

        init {
            taskCheckBox = view.findViewById(R.id.todoCheckBox)
            taskText = view.findViewById(R.id.task_text)
        }
    }
}