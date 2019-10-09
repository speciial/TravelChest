package com.speciial.travelchest.ui.preferences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.speciial.travelchest.MainActivity
import com.speciial.travelchest.MainActivity.Companion.PREF_NAME
import com.speciial.travelchest.PreferenceHelper.customPreference
import com.speciial.travelchest.PreferenceHelper.dark_theme
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
        root.findViewById<SwitchMaterial>(R.id.switch_save_online).isChecked = prefs.save_online
        root.findViewById<SwitchMaterial>(R.id.switch_save_online).setOnCheckedChangeListener { _, isChecked ->
            prefs.save_online = isChecked
        }
        root.findViewById<SwitchMaterial>(R.id.switch_darktheme).isChecked = prefs.dark_theme
        root.findViewById<SwitchMaterial>(R.id.switch_darktheme).setOnCheckedChangeListener { _, isChecked ->
            prefs.dark_theme = isChecked
            when(isChecked){
                true -> (activity as MainActivity).setTheme(R.style.DarkTheme_NoActionBar)
                false -> (activity as MainActivity).setTheme(R.style.AppTheme_NoActionBar)
            }
        }
        return root
    }


}