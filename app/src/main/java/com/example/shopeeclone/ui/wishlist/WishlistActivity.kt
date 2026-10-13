package com.example.shopeeclone.ui.wishlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shopeeclone.adapter.ProductAdapter
import com.example.shopeeclone.databinding.ActivityWishlistBinding
import com.example.shopeeclone.repository.ProductRepository
import com.example.shopeeclone.repository.WishlistRepository
import com.example.shopeeclone.ui.detail.ProductDetailActivity

class WishlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWishlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWishlistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val ids = WishlistRepository.getIds(this)
        val products = ProductRepository.getSampleProducts().filter { it.id in ids }
        binding.tvEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
        binding.rvProducts.adapter = ProductAdapter(
            products,
            onWishlistChanged = { refresh() }
        ) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product)
            startActivity(intent)
        }
    }
}