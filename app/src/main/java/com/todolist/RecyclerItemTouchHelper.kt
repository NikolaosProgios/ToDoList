package com.todolist

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.todolist.adapters.ToDoAdapter

class RecyclerItemTouchHelper(private val adapter: ToDoAdapter) : ItemTouchHelper.Callback() {

    private val context = adapter.getContext()

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.END  or ItemTouchHelper.START
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (direction == RIGHT) {
            adapter.editItem(position)
        } else if (direction == LEFT){
            adapter.deleteItem(position)
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

    companion object {
        private const val RIGHT = ItemTouchHelper.END
        private const val LEFT = ItemTouchHelper.START
    }
}