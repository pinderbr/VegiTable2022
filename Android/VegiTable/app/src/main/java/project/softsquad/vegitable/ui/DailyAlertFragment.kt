package project.softsquad.vegitable.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.softsquad.vegitable.R
import project.softsquad.vegitable.recyclerview.NotificationLogRecyclerView
import project.softsquad.vegitable.viewmodel.NotificationLogViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel


/**
 * Creaetd By: Jason Beattoe
 */
class DailyAlertFragment : Fragment() {

    private lateinit var mNotificationLogViewModel: NotificationLogViewModel
    private val userViewModel: UserViewModel by activityViewModels()

    var userId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)

        val adapter = NotificationLogRecyclerView()

        val recyclerView = view.findViewById<RecyclerView>(R.id.notification_recycler_view)

        recyclerView.adapter = adapter

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mNotificationLogViewModel = ViewModelProvider(this).get(NotificationLogViewModel::class.java)

        mNotificationLogViewModel.readAllData.observe(viewLifecycleOwner, Observer { notification ->
            adapter.setData(notification)
        })

        return view
    }
}