package com.example.shopeeclone.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object ContactDialog {
    // TODO: เปลี่ยนเป็นเบอร์และอีเมลจริงของร้าน
    private const val PHONE = "021234567"
    private const val EMAIL = "support@example.com"

    fun show(context: Context) {
        val items = arrayOf("โทร 02-123-4567", "ส่งอีเมลถึง $EMAIL")
        AlertDialog.Builder(context)
            .setTitle("ติดต่อเรา")
            .setItems(items) { _, which ->
                val intent = if (which == 0) Intent(Intent.ACTION_DIAL, Uri.parse("tel:$PHONE"))
                else Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$EMAIL"))
                try {
                    context.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "ไม่พบแอปที่รองรับบนเครื่องนี้", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("ปิด", null)
            .show()
    }
}