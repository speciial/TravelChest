package com.speciial.travelchest.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.PREF_NAME
import com.speciial.travelchest.PreferenceHelper.customPreference
import com.speciial.travelchest.PreferenceHelper.save_online
import com.speciial.travelchest.R


class PreferencesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_preferences, container, false)
        val prefs = customPreference(activity as MainActivity, PREF_NAME)
        root.findViewById<CheckBox>(R.id.preference_checkbox).isChecked = prefs.save_online
        root.findViewById<CheckBox>(R.id.preference_checkbox).setOnCheckedChangeListener { buttonView, isChecked ->
            prefs.save_online = isChecked
        }
        return root
    }


}