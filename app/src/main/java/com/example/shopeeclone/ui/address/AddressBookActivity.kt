package com.example.shopeeclone.ui.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopeeclone.databinding.ActivityAddressBookBinding
import com.example.shopeeclone.databinding.ItemSavedAddressBinding
import com.example.shopeeclone.model.SavedAddress
import com.example.shopeeclone.repository.AddressBookRepository

class AddressBookActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PICK_MODE = "extra_pick_mode"
        const val RESULT_ADDRESS_ID = "result_address_id"
    }

    private lateinit var binding: ActivityAddressBookBinding
    private var pickMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pickMode = intent.getBooleanExtra(EXTRA_PICK_MODE, false)
        if (pickMode) binding.toolbar.title = "เลือกที่อยู่จัดส่ง"
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.btnAddAddress.setOnClickListener {
            startActivity(Intent(this, AddressFormActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val list = AddressBookRepository.getAll(this)
        binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        binding.rvAddresses.adapter = AddressAdapter(list)
    }

    private fun confirmDelete(address: SavedAddress) {
        AlertDialog.Builder(this)
            .setTitle("ลบที่อยู่")
            .setMessage("ต้องการลบที่อยู่ของ ${address.recipientName} ใช่หรือไม่?")
            .setPositiveButton("ลบ") { _, _ ->
                AddressBookRepository.delete(this, address.id)
                refresh()
            }
            .setNegativeButton("ยกเลิก", null)
            .show()
    }

    private inner class AddressAdapter(
        private val items: List<SavedAddress>
    ) : RecyclerView.Adapter<AddressAdapter.VH>() {

        inner class VH(val b: ItemSavedAddressBinding) : RecyclerView.ViewHolder(b.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            VH(ItemSavedAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val a = items[position]
            with(holder.b) {
                tvNamePhone.text = "${a.recipientName} | ${a.phone}"
                tvAddress.text = a.fullAddress()
                tvDefaultBadge.visibility = if (a.isDefault) View.VISIBLE else View.GONE
                tvSetDefault.visibility = if (a.isDefault) View.GONE else View.VISIBLE

                tvSetDefault.setOnClickListener {
                    AddressBookRepository.setDefault(this@AddressBookActivity, a.id)
                    refresh()
                }
                tvEdit.setOnClickListener {
                    startActivity(
                        Intent(this@AddressBookActivity, AddressFormActivity::class.java)
                            .putExtra(AddressFormActivity.EXTRA_ADDRESS_ID, a.id)
                    )
                }
                tvDelete.setOnClickListener { confirmDelete(a) }
                root.setOnClickListener {
                    if (pickMode) {
                        setResult(Activity.RESULT_OK, Intent().putExtra(RESULT_ADDRESS_ID, a.id))
                        finish()
                    }
                }
            }
        }
    }
}