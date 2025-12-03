package com.example.cineverse_movie_app_two;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        setupLogoutPreference();
        setupThemePreference();

        setupPushNotificationsPreference();
        setupRecommendationNotificationsPreference();
        setupNewReleaseNotificationsPreference();
        setupPlaybackQualityPreference();
        setupAutoplayNextPreference();
        setupSubtitleLanguagePreference();
        setupSetupPinPreference();
        setupAgeFilterPreference();
        setupDownloadQualityPreference();
        setupManageDownloadsPreference();
        setupPersonalizedAdsPreference();
        setupManageDevicesPreference();
    }

    private void animatePreferenceClick(Preference preference) {
        if (getPreferenceScreen().findPreference(preference.getKey()) != null && getListView() != null) {
            Animation fade = new AlphaAnimation(0.3f, 1f);
            fade.setDuration(300);
            if (getView() != null) {
                getView().startAnimation(fade);
            }
        }
    }

    private void setupLogoutPreference() {
        Preference logoutPref = findPreference("logout");
        if (logoutPref != null) {
            logoutPref.setOnPreferenceClickListener(preference -> {
                animatePreferenceClick(preference);
                performLogout();
                return true;
            });
        }
    }

    private void performLogout() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void setupThemePreference() {
        ListPreference themePref = findPreference("theme");
        if (themePref != null) {
            themePref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);

                String themeValue = (String) newValue;
                switch (themeValue) {
                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                }
                Toast.makeText(requireContext(), "Theme updated", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupPushNotificationsPreference() {
        SwitchPreferenceCompat pref = findPreference("push_notifications");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                boolean enabled = (Boolean) newValue;
                Toast.makeText(requireContext(),
                        "Push notifications " + (enabled ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupRecommendationNotificationsPreference() {
        SwitchPreferenceCompat pref = findPreference("recommendation_notifications");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                boolean enabled = (Boolean) newValue;
                Toast.makeText(requireContext(),
                        "Recommendations " + (enabled ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupNewReleaseNotificationsPreference() {
        SwitchPreferenceCompat pref = findPreference("new_release_notifications");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                boolean enabled = (Boolean) newValue;
                Toast.makeText(requireContext(),
                        "New release notifications " + (enabled ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupPlaybackQualityPreference() {
        ListPreference pref = findPreference("playback_quality");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Playback quality set to " + newValue, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupAutoplayNextPreference() {
        SwitchPreferenceCompat pref = findPreference("autoplay_next");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                boolean enabled = (Boolean) newValue;
                Toast.makeText(requireContext(),
                        "Autoplay Next " + (enabled ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupSubtitleLanguagePreference() {
        ListPreference pref = findPreference("subtitle_language");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Subtitle language set to " + newValue, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupSetupPinPreference() {
        Preference pref = findPreference("setup_pin");
        if (pref != null) {
            pref.setOnPreferenceClickListener(preference -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Launch Parental PIN setup", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupAgeFilterPreference() {
        ListPreference pref = findPreference("age_filter");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Content restriction set to " + newValue, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupDownloadQualityPreference() {
        ListPreference pref = findPreference("download_quality");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Download quality set to " + newValue, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupManageDownloadsPreference() {
        Preference pref = findPreference("manage_downloads");
        if (pref != null) {
            pref.setOnPreferenceClickListener(preference -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Manage Downloads clicked", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupPersonalizedAdsPreference() {
        SwitchPreferenceCompat pref = findPreference("personalized_ads");
        if (pref != null) {
            pref.setOnPreferenceChangeListener((preference, newValue) -> {
                animatePreferenceClick(preference);
                boolean enabled = (Boolean) newValue;
                Toast.makeText(requireContext(),
                        "Personalized Ads " + (enabled ? "enabled" : "disabled"),
                        Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupManageDevicesPreference() {
        Preference pref = findPreference("manage_devices");
        if (pref != null) {
            pref.setOnPreferenceClickListener(preference -> {
                animatePreferenceClick(preference);
                Toast.makeText(requireContext(), "Manage Devices clicked", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }
}
