package com.todolist

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.todolist.adapters.ToDoAdapter

class RecyclerItemTouchHelper(private val adapter: ToDoAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val context = adapter.getContext()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == ItemTouchHelper.LEFT) {
            val builder = AlertDialog.Builder(context)
            with(builder) {
                setTitle(R.string.delete_task_title)
                setMessage(R.string.delete_task_body)
                setPositiveButton(R.string.confirm) { dialog, which -> adapter.deleteItem(position) }
                setNegativeButton(R.string.cancel) { dialog, which ->
                    adapter.notifyItemChanged(position)
                }
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
        } else {
            adapter.editItem(position)
        }
    }

    override fun onChildDraw(
        canvas: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val icon: Drawable
        val background: ColorDrawable
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        if (dX > 0) {// Swiping to the right
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit)!!
            background = ColorDrawable(
                ContextCompat.getColor(context, R.color.edit_task)
            )
        } else {
            icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete)!!
            background = ColorDrawable(ContextCompat.getColor(context, R.color.delete_task))
        }
        assert(icon != null)
        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight
        if (dX > 0) { // Swiping to the right
            val iconLeft = itemView.left + iconMargin
            val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.left, itemView.top,
                itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
            )
        } else if (dX < 0) { // Swiping to the left
            val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
            val iconRight = itemView.right - iconMargin
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            background.setBounds(
                itemView.right + dX.toInt() - backgroundCornerOffset,
                itemView.top, itemView.right, itemView.bottom
            )
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
        }
        background.draw(canvas)
        icon.draw(canvas)
    }
}