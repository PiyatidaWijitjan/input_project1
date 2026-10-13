package com.example.shopeeclone.model

data class SavedAddress(
    val id: String,
    val recipientName: String,
    val phone: String,
    val detail: String,
    val province: String,
    val district: String,
    val subDistrict: String,
    val zipcode: String,
    val isDefault: Boolean
) {
    fun fullAddress(): String = "$detail ต.$subDistrict อ.$district จ.$province $zipcode"
}