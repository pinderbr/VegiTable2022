package project.softsquad.vegitable.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.NotificationLogEntity

class NotificationLogRecyclerView: RecyclerView.Adapter<NotificationLogRecyclerView.MyViewHolder>(){

    //initializing an empty list of buckets
    private var notificationLogList = emptyList<NotificationLogEntity>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //creating objects to link with their UI elements
        val bucketIdTv: TextView = itemView.findViewById(R.id.nl_bucket_id_tv)
        val nlTime: TextView = itemView.findViewById(R.id.nl_time_tv)
        val nlMessage: TextView = itemView.findViewById(R.id.nl_message_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //linking an itemView to the bucket_card layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //for each item in the bucketList, set the image, text, and sensor name
        val currentItem = notificationLogList[position]

        holder.bucketIdTv.setText(currentItem.bucketId_fk.toString())
        holder.nlTime.setText(currentItem.notificationTime)
        holder.nlMessage.setText(currentItem.notificationMessage)
    }

    fun setData(notificationLogEntity: List<NotificationLogEntity>){
        this.notificationLogList = notificationLogEntity
        notifyDataSetChanged()
    }

    override fun getItemCount() = notificationLogList.size

}