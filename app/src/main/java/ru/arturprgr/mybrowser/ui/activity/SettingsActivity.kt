package ru.arturprgr.mybrowser.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.getDefaultBoolean

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) supportFragmentManager.beginTransaction()
            .replace(R.id.settings, SettingsFragment()).commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var savesHelper: SavesHelper
        private lateinit var defSearchSystem: Preference
        private lateinit var bottomToolsPanel: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            savesHelper = SavesHelper(requireContext())
            defSearchSystem = findPreference("def_search_system")!!
            bottomToolsPanel = findPreference("bottom_panel_tools")!!

            defSearchSystem.setOnPreferenceChangeListener { preference, newValue ->
                FirebaseHelper("${savesHelper.getAccount()}/preferences/${preference.key}")
                    .setValue(newValue)
                true
            }

            bottomToolsPanel.setOnPreferenceChangeListener { preference, newValue ->
                FirebaseHelper("${savesHelper.getAccount()}/preferences/${preference.key}")
                    .setValue(newValue)
                true
            }
        }
    }
}