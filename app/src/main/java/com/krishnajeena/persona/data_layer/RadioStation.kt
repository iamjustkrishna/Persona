package com.krishnajeena.persona.data_layer

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL
import javax.inject.Inject

@Serializable
data class RadioStation(
    val id: Int,
    val name: String,
    val genre: String,
    val streamUrl: String,
    val location: String
)

@Serializable
data class RadioLibraryResponse(
    val version: Int,
    val stations: List<RadioStation>
)

class RadioRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val jsonEngine: Json
){
    private val prefs = context.getSharedPreferences("radio_prefs", Context.MODE_PRIVATE)

    fun fetchAndCacheStations(): List<RadioStation>{
        return try {
            val jsonString = URL("https://raw.githubusercontent.com/iamjustkrishna/quote-api/refs/heads/main/radio_stations.json").readText()

            val response = jsonEngine.decodeFromString<RadioLibraryResponse>(jsonString)

            prefs.edit { putString("cached_stations", jsonString) }

            response.stations
        } catch(e: Exception){

            Log.e("Persona", "Failed to sync radio stations", e)

            loadCachedStations()
        }
    }

    fun loadCachedStations(): List<RadioStation> {
        val cached = prefs.getString("cached_stations", null)
        return if (cached != null){
            jsonEngine.decodeFromString<RadioLibraryResponse>(cached).stations
        } else{
            RadioLibrary.stations
        }
    }
}
object RadioLibrary {
    val stations = listOf(
        // --- LATEST POP & HIP-HOP (Global) ---

        RadioStation(1, "Alan Walker", "Walker", "https://stream.zeno.fm/kv9nt7z7vv8uv?aw_0_req_lsid=fe799c887d54c2861d565ca5feac911d", "Global"),
        RadioStation(2, "Justin Bieber", "POP", "https://stream.zeno.fm/1pppanydgfhvv?aw_0_req_lsid=fe799c887d54c2861d565ca5feac911d", "USA"),
        RadioStation(3, "The Weeknd", "Pop", "https://stream.zeno.fm/c3zv2erfselvv?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "UK"),
        RadioStation(4, "Taylor Swift", "Pop", "https://stream.zeno.fm/xiothj9jrxfvv?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "International"),

        // --- INDIAN TRENDING (Latest Hindi & Punjabi) ---
        RadioStation(5, "Arjst Singh", "Bollywood Hindi", "https://stream-158.zeno.fm/rfpf8qec94zuv?zt=eyJhbGciOiJIUzI1NiJ9.eyJzdHJlYW0iOiJyZnBmOHFlYzk0enV2IiwiaG9zdCI6InN0cmVhbS0xNTguemVuby5mbSIsInJ0dGwiOjUsImp0aSI6IlAzOXRNZlBIU21PZk9WNzBVeEI0N3ciLCJpYXQiOjE3NzEyMjQ0MzYsImV4cCI6MTc3MTIyNDQ5Nn0.KoBCKomsUMsHsmhsYMy2meGU4oUNBpyWvGnlpEEygUs&aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "India"),
        RadioStation(6, "Siddhu Moosewala", "Punjabi Hits", "https://stream.zeno.fm/e5hahqs7twzuv?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "India"),
        RadioStation(7, "Eminem", "Rap", "https://stream.zeno.fm/fscn84e3z14tv?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "USA"),

        // --- DEEP WORK & STUDY (High Signal) ---
        RadioStation(8, "Drake Playlist", "Mixed", "https://stream.zeno.fm/ai0d1xdhkoauv?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "Global"),
        RadioStation(9, "Soothing Piano", "Instrumental", "https://stream.zeno.fm/0r0xa792kwzuv", "Global"),

        // --- INTERNATIONAL PERSPECTIVE ---
        RadioStation(10, "Bob Dylan Exclusive", "Folk/Rock", "https://streaming.exclusive.radio/er/bobdylan/icecast.audio?aw_0_req_lsid=f5e9489c2303cb5302bfe247d7e03772", "USA"),
    )

    // Focus-friendly ambient music stations
    val focusStations = listOf(
        RadioStation(100, "Lofi Hip Hop", "Study Beats", "https://stream.zeno.fm/f3wvbbqmdg8uv", "Global"),
        RadioStation(101, "Deep Focus", "Ambient", "https://stream.zeno.fm/0r0xa792kwzuv", "Global"),
        RadioStation(102, "Calm Piano", "Classical", "https://stream.zeno.fm/3qp6w2wkg5zuv", "Global"),
        RadioStation(103, "Nature Sounds", "Ambient", "https://stream.zeno.fm/wcpry168vtzuv", "Global"),
    )
}