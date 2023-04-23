package com.todolist.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.todolist.models.ToDoModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    private lateinit var db: SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        // Create tables again
        onCreate(db)
    }

    fun openDatabase() {
        db = this.writableDatabase
    }

    fun insertTask(task: ToDoModel) {
        val cv = ContentValues()
        cv.put(TASK, task.text)
        cv.put(STATUS, 0)
        db.insert(TODO_TABLE, null, cv)
    }

    @SuppressLint("Range")
    fun getAllTasks(): ArrayList<ToDoModel> {
        val taskList: ArrayList<ToDoModel> = ArrayList()
        var cur: Cursor? = null
        db.beginTransaction()
        try {
            cur = db.query(
                TODO_TABLE, null, null, null, null, null, null, null
            )
            cur?.let {
                if (cur.moveToFirst()) {
                    do {
                        val task = ToDoModel(
                            id = cur.getInt(cur.getColumnIndex(ID)),
                            text = cur.getString(cur.getColumnIndex(TASK)),
                            status = cur.getInt(cur.getColumnIndex(STATUS))
                        )
                        taskList.add(task)
                    } while (cur.moveToNext())
                }
            }
        } finally {
            if (cur != null) {
                db.endTransaction()
                assert(cur != null)
                cur?.let { close() }
            }
        }
        return taskList
    }

    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues()
        cv.put(STATUS, status)
        db.update(TODO_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun updateTask(id: Int, task: String?) {
        val cv = ContentValues()
        cv.put(TASK, task)
        db.update(TODO_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db.delete(TODO_TABLE, "$ID= ?", arrayOf(id.toString()))
    }


    companion object {
        private val VERSION = 1
        private val NAME = "toDoListDatabase"
        private val TODO_TABLE = "todo"
        private val ID = "id"
        private val TASK = "task"
        private val STATUS = "status"
        private val CREATE_TODO_TABLE =
            ("CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TASK + " TEXT, " + STATUS + " INTEGER)")
    }
}