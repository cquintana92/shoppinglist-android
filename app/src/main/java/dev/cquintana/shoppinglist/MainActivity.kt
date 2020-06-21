package dev.cquintana.shoppinglist

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


interface DragListener {
    fun startDragging(holder: RecyclerView.ViewHolder)
}

class MainActivity : AppCompatActivity(), ShoppingListView, DragListener {

    private lateinit var adapter: ItemRecyclerViewAdapter
    private lateinit var presenter: ShoppingListPresenter
    private lateinit var preferencesManager: SharedPreferencesManager

    private val itemTouchHelper by lazy {
        // 1. Note that I am specifying all 4 directions.
        //    Specifying START and END also allows
        //    more organic dragging than just specifying UP and DOWN.
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                UP or
                        DOWN or
                        START or
                        END, 0
            ) {

                var dragFrom = -1
                var dragTo = -1

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.elevation = 8f
                    }
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    viewHolder.itemView.elevation = 0f

                    if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                        if (viewHolder is ItemRecyclerViewAdapter.ItemViewHolder) {
                            reallyMoved(dragFrom, dragTo, viewHolder.item!!)
                        }
                    }

                    dragFrom = -1
                    dragTo = -1
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    if (!adapter.isMovementValid(fromPosition, toPosition)) {
                        return false
                    }

                    if (dragFrom == -1) {
                        dragFrom = fromPosition
                    }
                    dragTo = toPosition

                    adapter.moveItem(fromPosition, toPosition)
                    adapter.notifyItemMoved(fromPosition, toPosition)

                    return true
                }

                private fun reallyMoved(from: Int, to: Int, item: ShoppingListItem) {
                    presenter.updatePosition(item.id, to)
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }

                override fun isItemViewSwipeEnabled(): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesManager = ShoppingListApplication.preferencesManager()
        initNightMode();
        setContentView(R.layout.activity_main)
        presenter = ShoppingListPresenter(this, ShoppingListApplication.shoppingListInteractor(), preferencesManager)

        setupUpdatedListeners()

        recyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemTouchHelper.attachToRecyclerView(recyclerview)
        swiperefreshlayout.setColorSchemeResources(
            R.color.colorAccent,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swiperefreshlayout.setOnRefreshListener {
            refresh()
        }
        adapter = ItemRecyclerViewAdapter(mutableListOf(), this)
        adapter.onItemChangeListener = { item, changeType ->
            this.onItemEdited(item, changeType)
        }
        recyclerview.adapter = adapter
        new_item_imageview.setOnClickListener {
            if (new_item_edittext.text.toString().isEmpty()) {
                new_item_edittext.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(new_item_edittext, InputMethodManager.SHOW_IMPLICIT)
            } else {
                createNewItem()
            }
        }

        new_item_edittext.onSubmit {
            createNewItem()
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_checked -> removeAllChecked()
            R.id.action_toggle_night_mode -> alternateNightMode()
            R.id.action_configure_base_url -> presenter.onConfigureServerUrlClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showSetupServerUrl(initialValue: String, cancelable: Boolean, callback: (String) -> Unit) {
        val edittext = EditTextExtended(this)
        edittext.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT or InputType.TYPE_TEXT_VARIATION_URI
        edittext.setText(initialValue)
        edittext.setHint(R.string.base_url_hint)

        val alert = AlertDialog.Builder(this)
            .setCancelable(cancelable)
            .setTitle(R.string.base_url_dialog_title)
            .setView(edittext)
            .setPositiveButton(R.string.new_element_dialog_confirm) { dialog, _ ->
                val value = edittext.text.toString()
                dialog.dismiss()
                callback(value)
            }
            .setNegativeButton(R.string.new_element_dialog_cancel) { dialog, _ -> dialog.dismiss() }

        val dialog = alert.create()
        dialog.show()

        edittext.onSubmit {
            val value = edittext.text.toString()
            dialog.dismiss()
            callback(value)
        }

        val margin = resources.getDimension(R.dimen.dialog_edittext_margin).toInt()
        edittext.setMarginExtensionFunction(left = margin, right = margin)
    }

    private fun refresh() {
        presenter.getAll()
    }

    private fun setupUpdatedListeners() {
        val ctx = this
        presenter.bind(object : Observer<Result> {
            override fun onComplete() {}
            override fun onSubscribe(d: Disposable?) {}

            override fun onNext(t: Result?) {
                Timber.d("Received items: ${t}")
                if (t != null) {
                    onUpdated(t)
                }
            }

            override fun onError(e: Throwable?) {
                Timber.e(e, "Error in requests")
                Toast.makeText(ctx, R.string.an_error_has_happened, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onUpdated(t: Result) {
        if (t.isOk()) {
            adapter.setItems(t.state!!)
        } else {
            when (val error = t.error!!) {
                is Error.BaseUrlNotConfigured ->
                    Toast.makeText(this, R.string.base_url_not_configured, Toast.LENGTH_LONG).show()
                is Error.IOError -> {
                    Timber.e(error.message, "IOError")
                    Toast.makeText(this, R.string.an_error_has_happened, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun onItemEdited(item: ShoppingListItem, change: ChangeType) {
        Timber.d("onItemEdited: [$item] [$change]")
        when (change) {
            ChangeType.Deleted -> processItemDeleted(item)
            ChangeType.ToBought -> toggleItem(item)
            ChangeType.ToNotBought -> toggleItem(item)
            ChangeType.Renamed -> renameItem(item)
        }
    }

    private fun toggleItem(item: ShoppingListItem) {
        presenter.toggleChecked(item.id)
    }

    private fun renameItem(item: ShoppingListItem) {
        presenter.updateName(item.id, item.name)
    }

    private fun processItemDeleted(item: ShoppingListItem) {
        presenter.deleteOne(item.id)
    }

    private fun addNewItem(name: String) {
        presenter.createNew(name)
    }

    private fun createNewItem() {
        val newItemName = new_item_edittext.text.toString()
        if (newItemName.isEmpty()) {
            new_item_edittext.setError(getString(R.string.new_element_cannot_be_empty))
            return
        }

        addNewItem(newItemName)
        new_item_edittext.setText("")
    }

    private fun removeAllChecked() {
        presenter.deleteAllChecked()
    }

    override fun showRefreshing() {
        swiperefreshlayout.isRefreshing = true
    }

    override fun hideRefreshing() {
        swiperefreshlayout.isRefreshing = false
    }

    private fun initNightMode() {
        val nightMode: Int = retrieveNightModeFromPreferences()
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun alternateNightMode() {
        alternateNightMode(getCurrentNightMode())
    }

    private fun getCurrentNightMode(): Int {
        return (resources.configuration.uiMode
                and Configuration.UI_MODE_NIGHT_MASK)
    }

    private fun alternateNightMode(currentNightMode: Int) {
        val newNightMode = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        saveNightModeToPreferences(newNightMode)
        recreate()
    }

    private fun retrieveNightModeFromPreferences(): Int = preferencesManager.getNightMode()
    private fun saveNightModeToPreferences(nightMode: Int) =
        preferencesManager.setNightMode(nightMode)

    override fun startDragging(holder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(holder)
    }

}