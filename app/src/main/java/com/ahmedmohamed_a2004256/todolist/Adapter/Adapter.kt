package com.ahmedmohamed_a2004256.todolist.Adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.provider.AlarmClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.ahmedmohamed_a2004256.todolist.Dao.Dao
import com.ahmedmohamed_a2004256.todolist.Models.Data
import com.ahmedmohamed_a2004256.todolist.Ui.EditNoteActivity
import com.ahmedmohamed_a2004256.todolist.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class Adapter(
    private var list: MutableList<Data>,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val userDao: Dao
) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item.title

        val hour = item.hour
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        val minute = item.minute
        val amPm =
            if (hour >= 12) context.getString(R.string.pm) else context.getString(R.string.am)
        val selectedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)

        holder.time.text = selectedTime

        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = item.isChecked

        holder.notification.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val isPM = hour >= 12
                val formattedHour = if (hour % 12 == 0) 12 else hour % 12
                val amPm =
                    if (isPM) context.getString(R.string.pm) else context.getString(R.string.am)
                val intent = Intent(AlarmClock.ACTION_SET_ALARM)
                intent.putExtra(AlarmClock.EXTRA_MESSAGE, item.title)
                intent.putExtra(AlarmClock.EXTRA_HOUR, formattedHour)
                intent.putExtra(AlarmClock.EXTRA_MINUTES, minute)
                intent.putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                context.startActivity(intent)
            }
        }

        holder.edit.setOnClickListener {
            val intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra("id", item.id)
            context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete_task))
                .setMessage(context.getString(R.string.sub_delete_task))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes,
                    DialogInterface.OnClickListener { arg0, arg1 ->
                        lifecycleOwner.lifecycleScope.launch {
                            try {
//                                withContext(Dispatchers.IO) {
//                                    userDao.deleteData(position)
//                                }
                                if (position >= 0 && position < list.size) {
                                    list.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, list.size)

                                    withContext(Dispatchers.IO) {
                                        userDao.deleteData(position)
                                    }

                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context,
                                            context.getString(R.string.toast_delete_task), Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }).create().show()
        }

        if (item.isChecked) {
            holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.title.paintFlags = holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked

            lifecycleOwner.lifecycleScope.launch {
                userDao.updateTaskChecked(item.id, isChecked)
            }

            if (isChecked) {
                holder.title.paintFlags = holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.title.paintFlags =
                    holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val time: TextView = view.findViewById(R.id.time)
        val edit: ImageView = view.findViewById(R.id.edit)
        val delete: ImageView = view.findViewById(R.id.delete)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val notification: Switch = view.findViewById(R.id.notification)
    }
}