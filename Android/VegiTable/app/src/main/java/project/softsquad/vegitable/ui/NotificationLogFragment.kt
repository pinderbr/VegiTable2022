package project.softsquad.vegitable.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import project.softsquad.vegitable.ApiInterface
import project.softsquad.vegitable.R
import project.softsquad.vegitable.entity.NotificationLogEntity
import project.softsquad.vegitable.entity.NotificationSettingsEntity
import project.softsquad.vegitable.recyclerview.NotificationLogRecyclerView
import project.softsquad.vegitable.viewmodel.NotificationLogViewModel
import project.softsquad.vegitable.viewmodel.UserViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationFragment : Fragment() {

    private lateinit var mNotificationLogViewModel: NotificationLogViewModel
    private lateinit var notificationCollectionAdapter: NotificationCollectionAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mNotificationLogViewModel = ViewModelProvider(this).get(NotificationLogViewModel::class.java)
        mNotificationLogViewModel.insertNotification()
        //ties collection adapter to new NotificationCollectionAdapter object
        notificationCollectionAdapter = NotificationCollectionAdapter(this)
        //ties viewPager object to viewpager element on layout
        viewPager = view.findViewById(R.id.pager)
        //assigning viewpager adapter to notification collection adapter
        viewPager.adapter = notificationCollectionAdapter
        //connecting tab layout object to tab layout ui element
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Daily Notifications"
            } else if (position == 1) {
                tab.text = "Alert Notifications"
            } else {
                tab.text = "Sensor Notifications"
            }
        }.attach()
    }
}

class NotificationCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    //number of tabs
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        return if (position == 0) {
            DailyNotificationFrag()
        } else if (position == 1) {
            AlertNotificationFrag()
        } else {
            SensorNotificationFrag()
        }
    }
}

class DailyNotificationFrag : Fragment() {
    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        apiInterface = ApiInterface.create()
        val userViewModel: UserViewModel by activityViewModels()
        userViewModel.readAllData.observe(viewLifecycleOwner, {
            getDailyNotifications(it.userId)
        })

        return view
    }

    private fun getDailyNotifications(userId: Long) {
        var alerts: List<NotificationLogEntity>?

        apiInterface.getNotificationLogs(userId, "Daily")
            .enqueue(object : Callback<List<NotificationLogEntity>> {
                override fun onResponse(call: Call<List<NotificationLogEntity>>?, response: Response<List<NotificationLogEntity>>? ) {
                    if (response?.body() != null) {
                        alerts = response.body() as List<NotificationLogEntity>
                        if (alerts != null) {
                            val adapter = NotificationLogRecyclerView()
                            val recyclerView = view?.findViewById<RecyclerView>(R.id.notification_recycler_view)
                            if (recyclerView != null) {
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                adapter.setData(alerts!!)
                            }

                        } else {
                            Log.i("NO LOGS", "no daily notification logs for the user")
                        }
                    } else {
                        if (response != null) {
                            Log.i("SETTINGS  ERROR", "Something went wrong! " + response.errorBody())
                        }
                    }
                }

                override fun onFailure(call: Call<List<NotificationLogEntity>>?, t: Throwable?) {
                    Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
                }
            })

    }
}

class AlertNotificationFrag : Fragment() {

    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        apiInterface = ApiInterface.create()
        val userViewModel: UserViewModel by activityViewModels()
        userViewModel.readAllData.observe(viewLifecycleOwner, {
            getAlertNotifications(it.userId)
        })
        return view
    }

    private fun getAlertNotifications(userId: Long) {
        var alerts: List<NotificationLogEntity>?

        apiInterface.getNotificationLogs(userId, "Alert")
            .enqueue(object : Callback<List<NotificationLogEntity>> {
                override fun onResponse(call: Call<List<NotificationLogEntity>>?, response: Response<List<NotificationLogEntity>>? ) {
                    if (response?.body() != null) {
                        alerts = response.body() as List<NotificationLogEntity>
                        if (alerts != null) {
                            val adapter = NotificationLogRecyclerView()
                            val recyclerView = view?.findViewById<RecyclerView>(R.id.notification_recycler_view)
                            if (recyclerView != null) {
                                recyclerView.adapter = adapter
                                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                adapter.setData(alerts!!)
                            }

                        } else {
                            Log.i("NO LOGS", "no alert notification logs for the user")
                        }
                    } else {
                        if (response != null) {
                            Log.i("SETTINGS  ERROR", "Something went wrong! " + response.errorBody())
                        }
                    }
                }

                override fun onFailure(call: Call<List<NotificationLogEntity>>?, t: Throwable?) {
                    Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
                }
            })

    }
}

class SensorNotificationFrag : Fragment() {
    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification_list, container, false)
        apiInterface = ApiInterface.create()
        val userViewModel: UserViewModel by activityViewModels()
        userViewModel.readAllData.observe(viewLifecycleOwner, {
            getSensorAlerts(it.userId)
        })

        return view
    }

    private fun getSensorAlerts(userId: Long) {
        var alerts: List<NotificationLogEntity>?

            apiInterface.getNotificationLogs(userId, "Device")
                .enqueue(object : Callback<List<NotificationLogEntity>> {
                    override fun onResponse(call: Call<List<NotificationLogEntity>>?, response: Response<List<NotificationLogEntity>>? ) {
                        if (response?.body() != null) {
                            alerts = response.body() as List<NotificationLogEntity>
                            if (alerts != null) {
                                val adapter = NotificationLogRecyclerView()
                                val recyclerView = view?.findViewById<RecyclerView>(R.id.notification_recycler_view)
                                if (recyclerView != null) {
                                    recyclerView.adapter = adapter
                                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                                    adapter.setData(alerts!!)
                                }

                            } else {
                                Log.i("NO LOGS", "no device notification logs for the user")
                            }
                        } else {
                            if (response != null) {
                                Log.i("SETTINGS  ERROR", "Something went wrong! " + response.errorBody())
                            }
                        }
                    }

                    override fun onFailure(call: Call<List<NotificationLogEntity>>?, t: Throwable?) {
                        Log.i("SETTINGS ERROR", "Something else went wrong! " + t.toString())
                    }
                })

    }
}