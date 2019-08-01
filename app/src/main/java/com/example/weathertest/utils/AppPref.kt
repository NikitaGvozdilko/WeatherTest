package com.example.weathertest.utils


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng

object AppPref {
    private val PREF_TOKEN: String = "token"
    private val PREF_PASSWORD: String = "password"
    private val PREF_PLACE_NAME: String = "place_name"
    private val PREF_LAT: String = "lat"
    private val PREF_LON: String = "lon"
    private val TAG = "AppPref"

    private fun getSharedPref(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun savePlaceName(context: Context, name: String) {
        getSharedPref(context).edit()
            .putString(PREF_PLACE_NAME, name)
            .apply()
    }

    fun getPlaceName(context: Context): String? {
        return getSharedPref(context).getString(PREF_PLACE_NAME, null)
    }

    fun saveCoords(context: Context, latLng: LatLng) {
        getSharedPref(context).edit()
            .putFloat(PREF_LAT, latLng.latitude.toFloat())
            .putFloat(PREF_LON, latLng.longitude.toFloat())
            .apply()
    }

    fun getCoords(context: Context): LatLng? {
        val lat = getSharedPref(context).getFloat(PREF_LAT, -1111.0f)
        val lon = getSharedPref(context).getFloat(PREF_LON, -1111.0f)
        if (lat == -1111.0f || lon == -1111.0f) return null
        return LatLng(lat.toDouble(), lon.toDouble())
    }

    fun getToken(context: Context) = getSharedPref(context).getString(PREF_TOKEN, null)

    fun cleanLoginData(context: Context) {
        getSharedPref(context).edit().remove(PREF_PLACE_NAME).remove(PREF_PASSWORD).remove(PREF_TOKEN).apply()
    }

}//no instance