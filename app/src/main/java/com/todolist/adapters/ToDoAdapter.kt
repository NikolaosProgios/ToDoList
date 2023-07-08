package com.todolist.adapters

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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

        holder.itemView.setOnClickListener { askAlertDialog(holder) }

        holder.taskTitle.text = Html.fromHtml("<b>${item.title}</b>")
        holder.taskNote.text = item.note
//        toggleStrikeThrough( holder.taskTitle, holder.taskNote, toBoolean(item.status!!))
        checkIfDone(holder.taskTitle, holder.taskNote, toBoolean(item.status!!))
        holder.taskCheckBox.isChecked = toBoolean(item.status!!)
        holder.taskCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id!!, 1)
            } else {
                db.updateStatus(item.id!!, 0)
            }
//            toggleStrikeThrough( holder.taskTitle, holder.taskNote, isChecked)
            checkIfDone(holder.taskTitle, holder.taskNote, isChecked)
        }
    }
    private fun checkIfDone(taskTitle: TextView, taskNote: TextView, isChecked: Boolean) {
        if (isChecked) {
            taskTitle.setTextColor(ContextCompat.getColor(taskTitle.context, R.color.grey))
            taskNote.setTextColor(ContextCompat.getColor(taskTitle.context, R.color.grey))
        } else {
            taskTitle.setTextColor(ContextCompat.getColor(taskTitle.context, R.color.task_title))
            taskNote.setTextColor(ContextCompat.getColor(taskTitle.context, R.color.task_note))
        }
    }
    private fun toggleStrikeThrough(taskTitle: TextView, taskNote: TextView, isChecked: Boolean) {
        if (isChecked) {
            taskTitle.paintFlags = taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            taskNote.paintFlags = taskNote.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            taskTitle.paintFlags = taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            taskNote.paintFlags = taskNote.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
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
        bundle.putString("title", item.title)
        bundle.putString("note", item.note)
        val fragment = TaskDialog()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, TaskDialog.TAG)
    }

    private fun askAlertDialog(holder: ViewHolder){
        val position = holder.adapterPosition
        val builder = AlertDialog.Builder(getContext())
        with(builder) {
            setTitle(R.string.modify_task_title)
            setMessage(R.string.delete_task_message)
            setPositiveButton(R.string.edit) { dialog, which -> editItem(position) }
            setNeutralButton(R.string.cancel) { dialog, which -> notifyItemChanged(position) }
            setNegativeButton(R.string.delete) { dialog, which -> deleteAlertDialog(position) }
        }
        val dialog = builder.create()
        with(dialog) {
            setOnShowListener {
                window?.setBackgroundDrawableResource(R.color.dialog_bg)
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.edit_task))
                getButton(AlertDialog.BUTTON_NEUTRAL)
                    .setTextColor(ContextCompat.getColor(context, R.color.neutral_btn))
                getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.delete_task))

            }
            show()
        }
    }

    fun deleteAlertDialog(position:Int){
        val builder = AlertDialog.Builder(getContext())
        with(builder) {
            setTitle(R.string.delete_task_title)
            setMessage(R.string.delete_task_message)
            setPositiveButton(R.string.confirm) { dialog, which -> deleteItem(position) }
            setNegativeButton(R.string.cancel) { dialog, which -> notifyItemChanged(position) }
        }
        val dialog = builder.create()
        with(dialog) {
            setOnShowListener {
                window?.setBackgroundDrawableResource(R.color.dialog_bg)
                getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.negative_btn))
                getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(context, R.color.positive_btn))
            }
            show()
        }
    }

    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        var taskCheckBox: CheckBox
        var taskTitle: TextView
        var taskNote: TextView

        init {
            taskCheckBox = view.findViewById(R.id.todoCheckBox)
            taskTitle = view.findViewById(R.id.task_title)
            taskNote = view.findViewById(R.id.task_note)
        }
    }
}