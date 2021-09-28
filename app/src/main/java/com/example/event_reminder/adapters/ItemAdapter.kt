package com.example.event_reminder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.event_reminder.Event
import com.example.event_reminder.R
import java.util.*

class ItemAdapter(private val context: Context, private val dataset: List<Event>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    var onItemClick: ((Event) -> Unit)? = null

    inner class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val personName: TextView = view.findViewById(R.id.nameTextView)
        val date: TextView = view.findViewById(R.id.dateTextView)
        val age: TextView = view.findViewById(R.id.agetextView)
        val image: ImageView = view.findViewById(R.id.eventimageView)
        init {
            view.setOnClickListener {
                onItemClick?.invoke(dataset[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val event = dataset[position]
        //getting Date in correct format
        val eventDate: String = getDateString(event.day!!, event.month!!, event.year!!)
        holder.apply {
            personName.text = event.name
            date.text = eventDate
            //calculating age according to event's day,month & year
            age.text = calculateAge(event.day!!, event.month!! - 1, event.year!!)
            //Setting image according to the event type
            if (event.eventType == 1)
                image.setImageDrawable(getDrawable(context, R.drawable.img_anniversary))
            else
                image.setImageDrawable(getDrawable(context, R.drawable.img_birthday))
        }
    }

    override fun getItemCount() = dataset.size

    /**
     * Returns Age of the event in Y.O., M.O. or D.O. according to the difference.
     */
    private fun calculateAge(day: Int, month: Int, year: Int): String {
        val today: Calendar = Calendar.getInstance()
        val birthDate: Calendar = Calendar.getInstance()
        birthDate.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)
        if (today.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH))
            age--
        else if (today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH)
        )
            age--
        //if the event is 0 Y.O.
        return if(age==0){
            // calculate month difference between current month and event month
            val monthDiff  = (today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH) + 12) % 12
            if(monthDiff==0){
                //calculate date difference between today and event date
                "${today.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH)} D.O."
            } else "$monthDiff M.O."
        } else "$age Y.O."
    }

    /**
     * Returns "Today" if the event date is of today and returns date is correct format otherwise.
     */
    private fun getDateString(day: Int, month: Int, year: Int): String {
        val today: Calendar = Calendar.getInstance()
        return if (year == today.get(Calendar.YEAR) && month - 1 == today.get(Calendar.MONTH)
            && day == today.get(Calendar.DAY_OF_MONTH)
        )
            context.getString(R.string.today)
        else "${day}-${month}-${year}"
    }
}