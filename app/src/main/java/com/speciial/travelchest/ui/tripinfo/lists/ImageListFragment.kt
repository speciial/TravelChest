package com.speciial.travelchest.ui.tripinfo.lists


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.R
import com.speciial.travelchest.database.TravelChestDatabase
import com.speciial.travelchest.model.Type
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ImageListFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.fragment_image_list, container, false)

        doAsync {
            val imageList =
                TravelChestDatabase.get(activity as MainActivity).tripDao().get(tripID!!)
                    .getFilesByType(Type.IMAGE)

            val rvAdapter = ImageRVAdapter(imageList, context!!)
            val rvManager = GridLayoutManager(context!!, 3)

            uiThread {
                root.findViewById<RecyclerView>(R.id.image_list_rv).apply {
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
        fun newInstance(tripID: Long) = ImageListFragment().apply {
            arguments = Bundle().apply {
                putLong("tripID", tripID)
            }
        }
    }
}
