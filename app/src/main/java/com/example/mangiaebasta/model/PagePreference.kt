package com.example.mangiaebasta.model

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

private val Context.pageDataStore: DataStore<Preferences> by preferencesDataStore(name = "page_prefs")

object PagePreferences {
    private val CURRENT_PAGE_KEY = stringPreferencesKey("current_page")
    private val MENU_ID_KEY = stringPreferencesKey("menu_id")

    fun savePageData(context: Context, currentPage: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                context.pageDataStore.edit { prefs ->
                    prefs[CURRENT_PAGE_KEY] = currentPage.substringBefore("/")
                    prefs[MENU_ID_KEY] = currentPage.substringAfter("details/", "").takeIf { it.isNotEmpty() }
                        ?: prefs.remove(MENU_ID_KEY)
                }
            }.onFailure { e ->
                Log.e("PagePreferences", "Errore nel salvataggio della pagina: ${e.localizedMessage}")
            }
        }
    }

    suspend fun getPageData(context: Context): String {
        return runCatching {
            context.pageDataStore.data
                .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
                .first()
        }.map { prefs ->
            val basePage = prefs[CURRENT_PAGE_KEY] ?: "menu"
            val menuId = prefs[MENU_ID_KEY]
            if (basePage == "details" && menuId != null) "details/$menuId" else basePage
        }.getOrElse {
            Log.e("PagePreferences", "Errore nel recupero della pagina: ${it.localizedMessage}")
            "menu"
        }
    }
}
