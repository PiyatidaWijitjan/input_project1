package com.example.shopeeclone.repository

import android.content.Context
import com.example.shopeeclone.model.SavedAddress
import org.json.JSONArray
import org.json.JSONObject

object AddressBookRepository {
    private const val PREFS_NAME = "shopee_clone_address_book"

    private fun key(context: Context) =
        "addresses_" + (UserRepository.getCurrentUser(context)?.phone ?: "guest")

    fun getAll(context: Context): List<SavedAddress> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(key(context), null) ?: return emptyList()
        val array = JSONArray(json)
        return (0 until array.length()).map { i ->
            val o = array.getJSONObject(i)
            SavedAddress(
                id = o.getString("id"),
                recipientName = o.getString("recipientName"),
                phone = o.getString("phone"),
                detail = o.getString("detail"),
                province = o.getString("province"),
                district = o.getString("district"),
                subDistrict = o.getString("subDistrict"),
                zipcode = o.getString("zipcode"),
                isDefault = o.getBoolean("isDefault")
            )
        }
    }

    fun get(context: Context, id: String): SavedAddress? = getAll(context).find { it.id == id }

    fun getDefault(context: Context): SavedAddress? {
        val all = getAll(context)
        return all.firstOrNull { it.isDefault } ?: all.firstOrNull()
    }

    fun save(context: Context, address: SavedAddress) {
        val list = getAll(context).toMutableList()
        val makeDefault = address.isDefault || list.none { it.id != address.id }
        val toStore = address.copy(isDefault = makeDefault)
        val index = list.indexOfFirst { it.id == address.id }
        if (index >= 0) list[index] = toStore else list.add(toStore)
        persist(context, if (makeDefault) list.map { it.copy(isDefault = it.id == toStore.id) } else list)
    }

    fun delete(context: Context, id: String) {
        val remaining = getAll(context).filter { it.id != id }
        val fixed = if (remaining.isNotEmpty() && remaining.none { it.isDefault })
            remaining.mapIndexed { i, a -> a.copy(isDefault = i == 0) } else remaining
        persist(context, fixed)
    }

    fun setDefault(context: Context, id: String) {
        persist(context, getAll(context).map { it.copy(isDefault = it.id == id) })
    }

    private fun persist(context: Context, list: List<SavedAddress>) {
        val array = JSONArray()
        list.forEach { a ->
            array.put(JSONObject().apply {
                put("id", a.id)
                put("recipientName", a.recipientName)
                put("phone", a.phone)
                put("detail", a.detail)
                put("province", a.province)
                put("district", a.district)
                put("subDistrict", a.subDistrict)
                put("zipcode", a.zipcode)
                put("isDefault", a.isDefault)
            })
        }
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(key(context), array.toString()).apply()
    }
}