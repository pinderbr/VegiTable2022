package project.softsquad.vegitable.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.NotificationLogEntity

class DailyNotificationRecyclerView: RecyclerView.Adapter<DailyNotificationRecyclerView.MyViewHolder>(){

    private var notificationList = emptyList<NotificationLogEntity>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //creating objects to link with their UI elements
        var bucketId: TextView = itemView.findViewById(R.id.nl_bucket_id_tv)
        var notificationTime: TextView = itemView.findViewById(R.id.nl_time_tv)
        var notificationMessage: TextView = itemView.findViewById(R.id.nl_message_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //linking an itemView to the notification card layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = notificationList[position]

        holder.bucketId.setText(currentItem.bucketId_fk)
        holder.notificationTime.setText(currentItem.notificationTime)
        holder.notificationMessage.setText(currentItem.notificationMessage)
    }

    fun setData(notificationLogEntity: List<NotificationLogEntity>){
        this.notificationList = notificationLogEntity
        notifyDataSetChanged()
    }

    override fun getItemCount() = notificationList.size

}