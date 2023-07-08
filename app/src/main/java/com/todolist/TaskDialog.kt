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
import androidx.fragment.app.DialogFragment
import com.todolist.databinding.TaskDialogBinding
import com.todolist.models.ToDoModel
import com.todolist.utils.DatabaseHandler

class TaskDialog : DialogFragment() {
    private lateinit var db: DatabaseHandler
    private lateinit var binding: TaskDialogBinding
    var isUpdate = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.task_dialog, container, false)
        binding = TaskDialogBinding.bind(view)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isUpdate = false
        val bundle = arguments
        bundle?.let {
            isUpdate = true
            val title = bundle.getString("title")
            val note = bundle.getString("note")
            binding.taskDialogTextTitleEt.setText(title)
            binding.taskDialogTextBodyEt.setText(note)
            binding.taskDialogTitle.text = getString(R.string.task_title,getString(R.string.edit))
            if (note != null && note.isNotEmpty() || title != null && title.isNotEmpty() ) {
                binding.taskDialogSaveBtn.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.edit_task)
                )
            }
        }
        binding.taskDialogTitle.text = getString(R.string.task_title,getString(R.string.new_x))
        activity?.let { db = DatabaseHandler(it) }
        db.openDatabase()

        binding.taskDialogTextTitleEt.addTextChangedListener(textWatcher)
        binding.taskDialogTextBodyEt.addTextChangedListener(textWatcher)

        binding.taskDialogSaveBtn.setOnClickListener {
            val title = binding.taskDialogTextTitleEt.text.toString()
            val note = binding.taskDialogTextBodyEt.text.toString()
            if (isUpdate) {
                bundle?.getInt("id")?.let { it1 -> db.updateTask(it1, title, note) }
            } else {
                db.insertTask(ToDoModel(title = title, note = note, status = 0))
            }
            dismiss()
        }
    }

    private var textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            with(binding.taskDialogSaveBtn) {
                if (s.toString() == "") {
                    isEnabled = false
                    setTextColor(ContextCompat.getColor(requireContext(),R.color.pre_save_task))
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
        val TAG = "DialogFragment"
        fun newInstance() = TaskDialog()
    }
}