package com.example.speedcamalert

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.example.speedcamalert.classes.Patrol
import java.text.SimpleDateFormat
import java.util.Locale

class PatrolAdapter(private val context: Context, private val patrolListLiveData: LiveData<List<Patrol>>) : BaseAdapter() {

    private var patrolList: List<Patrol> = emptyList()

    init {
        // Observe the LiveData and update the local list when data changes
        patrolListLiveData.observeForever { patrolList ->
            this.patrolList = patrolList
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return patrolList.size
    }

    override fun getItem(p0: Int): Any {
        return patrolList[p0]
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var myView = convertView
        val holder: ViewHolder

        if (myView == null) {
            myView = LayoutInflater.from(context).inflate(R.layout.custom_patrol_listview, parent, false)
            holder = ViewHolder(myView)
            myView.tag = holder
        } else {
            holder = myView.tag as ViewHolder
        }

        val patrol = patrolList[position]
        holder.patrolNameTextView.text = patrol.name
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formatted = formatter.format(patrol.date)
        holder.patrolDateTextView.text = formatted
        if(patrol.type=="Patrol"){
            holder.patrolTypeTextView.text = "Patrola"
        }
        else{
            holder.patrolTypeTextView.text = "Radar"
        }
        holder.patrolDescriptionTextView.text = patrol.description
        holder.latitudeTextView.text = "Latitude: ${patrol.latitude}"
        holder.longitudeTextView.text = "Longitude: ${patrol.longitude}"

        return myView!!
    }
    private class ViewHolder(view: View) {
        val patrolNameTextView: TextView = view.findViewById(R.id.patrolNameTextView)
        val patrolDateTextView: TextView = view.findViewById(R.id.patrolDateTextView)
        val patrolTypeTextView: TextView = view.findViewById(R.id.patrolTypeTextView)
        val patrolDescriptionTextView: TextView = view.findViewById(R.id.patrolDescriptionTextView)
        val latitudeTextView: TextView = view.findViewById(R.id.latitudeTextView)
        val longitudeTextView: TextView = view.findViewById(R.id.longitudeTextView)
    }

    fun updatePatrolList(newList: List<Patrol>) {
        patrolList = newList
        Log.d("IZMENE",patrolList.size.toString())
        notifyDataSetChanged()

    }
}