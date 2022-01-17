package project.softsquad.vegitable.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.entity.PlantsEntity
import java.lang.ClassCastException

class BucketListRecyclerView(var bucketListFragment: Fragment, private val onItemClicked: (BucketEntity) -> Unit): RecyclerView.Adapter<BucketListRecyclerView.MyViewHolder>(){

    interface EditBucketDialogListener {
        fun onEditDialogPositiveClick(holder: MyViewHolder, currentBucket: BucketEntity)
    }

    interface ArchiveBucketDialogListener {
        fun onArchiveDialogPositiveClick(holder: MyViewHolder, currentBucket: BucketEntity)
    }
    lateinit var editListener: EditBucketDialogListener
    lateinit var archiveListener: ArchiveBucketDialogListener

    //initializing an empty list of buckets
    private var bucketList = emptyList<BucketEntity>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        //creating objects to link with their UI elements
        val bucketImage: ImageView = itemView.findViewById(R.id.bucket_image_view)
        val bucketName: TextView = itemView.findViewById(R.id.bucket_name)
        val bucketSensorName: TextView = itemView.findViewById(R.id.sensor_text_view)
        val editBucketButton: ImageView = itemView.findViewById(R.id.bucket_edit_button)
        val archiveBucketButton: ImageView = itemView.findViewById(R.id.archive_bucket_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //linking an itemView to the bucket_card layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bucket_card, parent, false)
        try {
            editListener = bucketListFragment as EditBucketDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $bucketListFragment must implement EditBucketDialogListener")
        }
        try {
            archiveListener = bucketListFragment as ArchiveBucketDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("ERROR -> $bucketListFragment must implement ArchiveBucketDialogListener")
        }

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //for each item in the bucketList, set the image, text, and sensor name
        val currentBucket = bucketList[position]
        holder.bucketImage.setImageResource(R.drawable.vector_grass)//change to imgResource
        holder.bucketName.setText(currentBucket.bucketName)
        holder.bucketSensorName.setText("" + currentBucket.deviceId_fk)



        holder.itemView.setOnClickListener{
            onItemClicked(currentBucket)
        }
        holder.editBucketButton.setOnClickListener {
            editListener.onEditDialogPositiveClick(holder, currentBucket)
        }
        holder.archiveBucketButton.setOnClickListener {
            archiveListener.onArchiveDialogPositiveClick(holder, currentBucket)
        }
    }

    fun setData(bucketList: List<BucketEntity>){  // , plantList: List<PlantsEntity>){
        this.bucketList = bucketList
        notifyDataSetChanged()
    }

    override fun getItemCount() = bucketList.size

}