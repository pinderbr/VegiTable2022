package project.softsquad.vegitable.ui.bucket

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.BucketEntity
import project.softsquad.vegitable.viewmodel.bucket.BucketViewModel
import project.softsquad.vegitable.recyclerview.BucketListRecyclerView
import project.softsquad.vegitable.viewmodel.UserViewModel
import project.softsquad.vegitable.viewmodel.plant.PlantViewModel


/**
 * Author: Jason Beattie
 * Modified by: Brianna McBurney
 * Description:
 */

class BucketListFragment : Fragment(), BucketListRecyclerView.EditBucketDialogListener,
    BucketListRecyclerView.ArchiveBucketDialogListener {

    //view models
    private val mBucketViewModel: BucketViewModel by activityViewModels()
    private val plantViewModel: PlantViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    var userId: Long = 0
//    var tempBucketId: Long = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflate the fragment_bucket_list layout
        val view = inflater.inflate(R.layout.fragment_bucket_list, container, false)

        //receive the user id from the activity, so we can insert it on live data in view model, or pass it to next fragments
        val bundle = arguments
        if (bundle != null) {
            userId = bundle.getLong("user")
            Log.i("TEST", "user id passed in is: $userId")
        }


        //sets the passed in user id to live data - it can now be accessed via observer from any fragment
        userViewModel.setUserId(userId)

        val adapter = BucketListRecyclerView(this){
            val action = BucketListFragmentDirections.actionBucketListFragmentToViewPlantListFragment(it.bucketId, it.userId_fk.toLong())
            findNavController().navigate(action)
        }

        //linking the recyclerView object to the bucket_recycler_view layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.bucket_recycler_view)

        //linking the recyclerView layout to the BucketListRecyclerView() adapter class
        recyclerView.adapter = adapter

        //defining the layout manager which is responsible for measuring and positioning the card views within the recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //provides ViewModels for this fragment (BucketViewModel)
        //mBucketViewModel = ViewModelProvider(this).get(BucketViewModel::class.java)
        //plantViewModel = ViewModelProvider(this).get(PlantViewModel::class.java)

        //calling readAllData from BucketViewModel is a select all query from the database
        mBucketViewModel.readAllData.observe(viewLifecycleOwner, {  bucketList ->
            adapter.setData(bucketList)
        })





        //TODO pass in the bucket id you have clicked on and pass it in here to save to livedata as "current bucket" (if needed)
//        mBucketViewModel.setCurrentBucketId(tempBucketId)

        //navigation for add_bucket_button
        view.findViewById<Button>(R.id.add_bucket_button).setOnClickListener {
            findNavController().navigate(R.id.action_bucketListFragment_to_addBucketFragment)

        }

        return view
    }

    override fun onEditDialogPositiveClick(holder: BucketListRecyclerView.MyViewHolder, currentBucket: BucketEntity ) {
        val action = BucketListFragmentDirections.actionBucketListFragmentToUpdateBucketFragment(currentBucket.bucketId)
        findNavController().navigate(action)
    }

    override fun onArchiveDialogPositiveClick(holder: BucketListRecyclerView.MyViewHolder, currentBucket: BucketEntity ) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Archive Bucket")
            .setMessage("Are you sure you want to archive ${currentBucket.bucketName}?")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id -> mBucketViewModel.archiveBucket(currentBucket) })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create()
        builder.show()
    }

}