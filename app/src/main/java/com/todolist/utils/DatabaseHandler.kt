package com.todolist.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.todolist.models.ToDoModel


/**
context: Context)
private var context = context

MIX

 */


class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {

    private lateinit var db: SQLiteDatabase
    private var context = context

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TODO_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        onCreate(db)
    }

    fun openDatabase() {
        db = this.writableDatabase
    }

    fun insertTask(task: ToDoModel) {
        val cv = ContentValues()
        cv.put(TITLE, task.title)
        cv.put(NOTE, task.note)
        cv.put(STATUS, 0)
        db.insert(TODO_TABLE, null, cv)
    }

    @SuppressLint("Range")
    fun getAllTasks(): ArrayList<ToDoModel> {
        val taskList: ArrayList<ToDoModel> = ArrayList()
        var cur: Cursor? = null
        try { db.beginTransaction() } catch (e:Exception){}
        try {
            cur = db.query(
                TODO_TABLE, null, null, null, null, null, null, null
            )
            cur?.let {
                if (cur.moveToFirst()) {
                    do {
                        val task = ToDoModel(
                            id = cur.getInt(cur.getColumnIndex(ID)),
                            title = cur.getString(cur.getColumnIndex(TITLE)),
                            note = cur.getString(cur.getColumnIndex(NOTE)),
                            status = cur.getInt(cur.getColumnIndex(STATUS))
                        )
                        taskList.add(task)
                    } while (cur.moveToNext())
                }
            }
        }
        catch (e:Exception){
            Toast.makeText(context, "Something goes wrong! Please close and re-open the app.", Toast.LENGTH_LONG).show()
            println("Exception $e")
        }
        finally {
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

    fun updateTask(id: Int, title: String?, note: String?) {
        val cv = ContentValues()
        cv.put(TITLE, title)
        cv.put(NOTE, note)
        db.update(TODO_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db.delete(TODO_TABLE, "$ID= ?", arrayOf(id.toString()))
    }

    companion object {
        private val VERSION = 1
        private const val NAME = "toDoListDatabase"
        private const val TODO_TABLE = "todo"
        private const val ID = "id"
        private const val TITLE = "title"
        private const val NOTE = "note"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE = ("CREATE TABLE " +
                TODO_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE  + " TEXT, " +
                NOTE   + " TEXT, " +
                STATUS + " INTEGER)")
    }
}