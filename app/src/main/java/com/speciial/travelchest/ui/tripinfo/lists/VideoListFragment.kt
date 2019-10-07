package com.speciial.travelchest.ui.tripinfo.lists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Type
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class VideoListFragment : Fragment() {

    private var tripID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tripID = it.getLong("tripID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_video_list, container, false)

        doAsync {
            val videoList =
                TravelChestDatabase.get(activity as MainActivity).tripDao().get(tripID!!)
                    .getFilesByType(Type.VIDEO)
            val rvAdapter = VideoRVAdapter(videoList, context!!)
            val rvManager = LinearLayoutManager(context!!)

            uiThread {
                root.findViewById<RecyclerView>(R.id.video_list_rv).apply {
                    setHasFixedSize(true)
                    adapter = rvAdapter
                    layoutManager = rvManager
                }
            }
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(tripID: Long) = VideoListFragment().apply {
            arguments = Bundle().apply {
                putLong("tripID", tripID)
            }
        }
    }

}
