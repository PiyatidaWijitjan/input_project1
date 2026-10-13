package com.example.shopeeclone.repository

import android.content.Context
import com.example.shopeeclone.model.User
import org.json.JSONArray
import org.json.JSONObject

object UserRepository {

    private const val PREFS_NAME = "shopee_clone_prefs"
    private const val KEY_USERS = "registered_users"
    private const val KEY_CURRENT_PHONE = "current_user_phone"

    sealed class RegisterResult {
        object Success : RegisterResult()
        object PhoneAlreadyExists : RegisterResult()
    }

    fun register(context: Context, user: User): RegisterResult {
        val users = getAllUsers(context)
        if (users.any { it.phone == user.phone }) {
            return RegisterResult.PhoneAlreadyExists
        }
        users.add(user)
        saveAllUsers(context, users)
        setCurrentUser(context, user.phone)
        return RegisterResult.Success
    }

    fun login(context: Context, phone: String, password: String): User? {
        val user = getAllUsers(context).find { it.phone == phone && it.password == password }
        if (user != null) setCurrentUser(context, user.phone)
        return user
    }

    fun getCurrentUser(context: Context): User? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val phone = prefs.getString(KEY_CURRENT_PHONE, null) ?: return null
        return getAllUsers(context).find { it.phone == phone }
    }

    fun isLoggedIn(context: Context): Boolean = getCurrentUser(context) != null

    fun logout(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_CURRENT_PHONE).apply()
    }

    enum class ChangePasswordResult { SUCCESS, WRONG_CURRENT, SAME_AS_OLD, NOT_LOGGED_IN }

    fun updateProfile(context: Context, name: String, email: String): Boolean {
        val current = getCurrentUser(context) ?: return false
        val users = getAllUsers(context)
        val index = users.indexOfFirst { it.phone == current.phone }
        if (index < 0) return false
        users[index] = current.copy(name = name, email = email)
        saveAllUsers(context, users)
        return true
    }

    fun changePassword(context: Context, currentPassword: String, newPassword: String): ChangePasswordResult {
        val current = getCurrentUser(context) ?: return ChangePasswordResult.NOT_LOGGED_IN
        if (current.password != currentPassword) return ChangePasswordResult.WRONG_CURRENT
        if (newPassword == currentPassword) return ChangePasswordResult.SAME_AS_OLD
        val users = getAllUsers(context)
        val index = users.indexOfFirst { it.phone == current.phone }
        users[index] = current.copy(password = newPassword)
        saveAllUsers(context, users)
        return ChangePasswordResult.SUCCESS
    }

    /** จำลองการยืนยันตัวตน: เบอร์ + อีเมลต้องตรงกับที่สมัครไว้ (แอปจริงควรใช้ OTP ผ่านเซิร์ฟเวอร์) */
    fun resetPassword(context: Context, phone: String, email: String, newPassword: String): Boolean {
        val users = getAllUsers(context)
        val index = users.indexOfFirst { it.phone == phone && it.email.equals(email, ignoreCase = true) }
        if (index < 0) return false
        users[index] = users[index].copy(password = newPassword)
        saveAllUsers(context, users)
        return true
    }

    private fun setCurrentUser(context: Context, phone: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENT_PHONE, phone).apply()
    }

    private fun getAllUsers(context: Context): MutableList<User> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_USERS, null) ?: return mutableListOf()

        val array = JSONArray(json)
        val users = mutableListOf<User>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            users.add(
                User(
                    name = obj.getString("name"),
                    phone = obj.getString("phone"),
                    email = obj.getString("email"),
                    password = obj.getString("password")
                )
            )
        }
        return users
    }

    private fun saveAllUsers(context: Context, users: List<User>) {
        val array = JSONArray()
        users.forEach { user ->
            val obj = JSONObject()
            obj.put("name", user.name)
            obj.put("phone", user.phone)
            obj.put("email", user.email)
            obj.put("password", user.password)
            array.put(obj)
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_USERS, array.toString()).apply()
    }
}