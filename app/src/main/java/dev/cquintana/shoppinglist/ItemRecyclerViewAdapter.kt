package dev.cquintana.shoppinglist

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

enum class ChangeType {
    ToBought,
    ToNotBought,
    Deleted,
    Renamed
}

typealias ItemChangeListener = (ShoppingListItem, ChangeType) -> Unit

data class Change<out T>(
    val oldData: T,
    val newData: T
)

fun <T> createCombinedPayload(payloads: List<Change<T>>): Change<T> {
    val firstChange = payloads.first()
    val lastChange = payloads.last()

    return Change(firstChange.oldData, lastChange.newData)
}

class ItemDiffUtilCallback(
    private var oldItems: List<ShoppingListItem>,
    private var newItems: List<ShoppingListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldItems.size

    override fun getNewListSize(): Int = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition].id == newItems[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItems[oldItemPosition] == newItems[newItemPosition]

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]

        return Change(
            oldItem,
            newItem)
    }
}

class ItemRecyclerViewAdapter(private var items: MutableList<ShoppingListItem>, private var dragListener: DragListener) :
    RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder>() {

    var onItemChangeListener: ItemChangeListener? = null

    inner class ItemViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.element_row, parent, false)) {

        private var checkBox: CheckBox? = null
        private var nameEditText: EditText? = null
        private var confirmImageView: ImageView? = null
        private var deleteImageView: ImageView? = null
        var dragHandlerView: ImageView? = null
        private var divider: View? = null
        var item: ShoppingListItem? = null
        private var textChangeListener = object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (item?.name != s?.toString()) {
                    confirmImageView?.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        init {
            checkBox = itemView.findViewById(R.id.row_checkbox)
            nameEditText = itemView.findViewById(R.id.row_textview)
            deleteImageView = itemView.findViewById(R.id.delete_imageview)
            dragHandlerView = itemView.findViewById(R.id.row_drag_indicator)
            confirmImageView = itemView.findViewById(R.id.confirm_change_imageview)
            divider = itemView.findViewById(R.id.divider)
        }

        fun setData(item: ShoppingListItem, showDivider: Boolean, listener: ItemChangeListener?) {
            this.item = item
            val isBought = item.checked

            divider?.visibility = if (showDivider) { View.VISIBLE } else { View.INVISIBLE }

            val textColor = if (isBought) {
                R.color.item_inactive_text_color
            } else {
                R.color.item_active_text_color
            }

            nameEditText?.error = null
            nameEditText?.setTextColor(nameEditText?.resources?.getColor(textColor)!!)

            deleteImageView?.visibility = View.GONE
            confirmImageView?.visibility = View.GONE
            checkBox?.setOnCheckedChangeListener(null)
            checkBox?.isChecked = isBought
            checkBox?.setOnCheckedChangeListener { _, isChecked ->
                val type = if (isChecked) {
                    ChangeType.ToBought
                } else {
                    ChangeType.ToNotBought
                }
                listener?.invoke(item, type)
            }

            nameEditText?.setText(item.name)

            if (nameEditText?.isFocused ?: false) {
                deleteImageView?.visibility = View.VISIBLE
            }
            nameEditText?.removeTextChangedListener(textChangeListener)
            nameEditText?.addTextChangedListener(textChangeListener)
            nameEditText?.setOnFocusChangeListener { _, hasFocus ->
                deleteImageView?.visibility = if (hasFocus) { View.VISIBLE } else { View.GONE }
            }
            nameEditText?.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    val newText = nameEditText?.text.toString()
                    if (newText.isEmpty()) {
                        nameEditText?.setError(nameEditText?.resources?.getString(R.string.new_element_cannot_be_empty))
                    } else {
                        confirmImageView?.visibility = View.GONE
                        item.name = newText
                        nameEditText?.isEnabled = false
                        listener?.invoke(item, ChangeType.Renamed)
                        nameEditText?.isEnabled = true
                        nameEditText?.isCursorVisible = false
                    }
                }
                true

            }
            confirmImageView?.setOnClickListener { _ ->
                val newText = nameEditText?.text.toString()
                if (newText.isEmpty()) {
                    nameEditText?.setError(nameEditText?.resources?.getString(R.string.new_element_cannot_be_empty))
                } else {
                    item.name = newText
                    listener?.invoke(item, ChangeType.Renamed)
                }
            }
            deleteImageView?.setOnClickListener { _ ->
                listener?.invoke(item, ChangeType.Deleted)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val holder = ItemViewHolder(inflater, parent)
        holder.dragHandlerView?.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                // 2. When we detect touch-down event, we call the
                //    startDragging(...) method we prepared above
                dragListener.startDragging(holder)
            }
            return@setOnTouchListener true
        }
        return holder
    }

    override fun getItemCount(): Int {
        return this.items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setData(this.items[position], isLastUnchecked(position), onItemChangeListener)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val combinedChange = createCombinedPayload(payloads as List<Change<ShoppingListItem>>)
            holder.setData(combinedChange.newData, isLastUnchecked(position), onItemChangeListener)
        }
    }

    fun isLastUnchecked(position: Int): Boolean {
        val current = this.items[position]
        if (current.checked) {
            return false
        } else {
            if (position + 1 == this.items.size) {
                return false
            } else {
                return this.items[position + 1].checked
            }
        }
    }

    fun setItems(items: List<ShoppingListItem>) {
        val result = DiffUtil.calculateDiff(ItemDiffUtilCallback(this.items, items))
        result.dispatchUpdatesTo(this)
        this.items = items.toMutableList()
        postDelayed(500) {
            this.notifyDataSetChanged()
        }
    }

    fun moveItem(from: Int, to: Int) {
        val fromItem = items[from]
        items.removeAt(from)
        if (to < from) {
            items.add(to, fromItem)
        } else {
            items.add(to - 1, fromItem)
        }
    }

    fun isMovementValid(from: Int, to: Int): Boolean {
        return items[from].checked == items[to].checked
    }
}
