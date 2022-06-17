package com.qhy040404.libraryonetap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.ui.AboutActivity
import rikka.preference.SimpleMenuPreference

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SimpleMenuPreference>(Constants.PREF_DARK)?.apply {
            setOnPreferenceChangeListener { _, _ ->
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.apply {
            setOnPreferenceChangeListener { _, _ ->
                GlobalManager.lazyMgr.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_LOCALE)?.apply {
            setOnPreferenceChangeListener { _, _ ->
                activity?.recreate()
                true
            }
        }

        findPreference<Preference>(Constants.PREF_ABOUT)?.apply {
            setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
        }
    }
}