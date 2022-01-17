package project.softsquad.vegitable.ui

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import project.softsquad.vegitable.R
import project.softsquad.vegitable.ui.bucket.ViewBucketDataCurrentFragment
import project.softsquad.vegitable.ui.bucket.ViewBucketDataHistoricalFragment

/**
 * Author: Brianna McBurney
 * Description:
 */
@RequiresApi(Build.VERSION_CODES.O)
class BucketDataFragment : Fragment() {

    private val navigationArgs: BucketDataFragmentArgs by navArgs()
    private lateinit var bucketDataCollectionAdapter: BucketDataCollectionAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bucket_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //ties collection adapter to new NotificationCollectionAdapter object
        bucketDataCollectionAdapter = BucketDataCollectionAdapter(this, navigationArgs.bucketId)
        //ties viewPager object to viewpager element on layout
        viewPager = view.findViewById(R.id.pager)
        //assigning viewpager adapter to notification collection adapter
        viewPager.adapter = bucketDataCollectionAdapter
        //connecting tab layout object to tab layout ui element
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            if (position == 0) {
                tab.text = "Current"
            } else if (position == 1) {
                tab.text = "Historical"
            }
        }.attach()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class BucketDataCollectionAdapter(fragment: Fragment, var bucketId: Long) : FragmentStateAdapter(fragment) {
    //number of tabs
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

        return if (position == 0) {
            ViewBucketDataCurrentFragment(bucketId)
        } else {
            ViewBucketDataHistoricalFragment(bucketId)
        }
    }
}