package com.todolist

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.todolist.databinding.NewTaskBinding
import com.todolist.models.ToDoModel
import com.todolist.utils.DatabaseHandler

class AddNewTask : BottomSheetDialogFragment() {

    private lateinit var db: DatabaseHandler
    private lateinit var binding: NewTaskBinding
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task, container, false)
        binding = NewTaskBinding.bind(view)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUpdate = false
        val bundle = arguments
        bundle?.let {
            isUpdate = true
            val task = bundle.getString("task")
            binding.newTaskEt.setText(task)
            if (task != null && task.isNotEmpty()) {
                binding.newTaskSaveBtn.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.edit_task)
                )
            }
        }
        activity?.let { db = DatabaseHandler(it) }
        db.openDatabase()

        binding.newTaskEt.addTextChangedListener(textWatcher)

        binding.newTaskSaveBtn.setOnClickListener {
            val text = binding.newTaskEt.text.toString()
            if (isUpdate) {
                bundle?.getInt("id")?.let { it1 -> db.updateTask(it1, text) }
            } else {
                db.insertTask(ToDoModel(text = text, status = 0))
            }
            dismiss()
        }
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding.newTaskSaveBtn) {
                if (s.toString() == "") {
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.pre_save_task))
                } else if (s.toString() !== "" && isUpdate) {
                    isEnabled = true
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.edit_task))
                } else {
                    isEnabled = true
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.save_task))
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {}
    }

    override fun onDismiss(dialog: DialogInterface) {
        val activity: Activity? = activity
        if (activity is DialogCloseListener) {
            (activity as DialogCloseListener).handleDialogClose(dialog)
        }
    }

    companion object {
        val TAG = "ActionBottomDialog"
        fun newInstance() = AddNewTask()
    }
}