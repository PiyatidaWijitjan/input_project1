package com.example.shopeeclone.ui.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopeeclone.databinding.ActivityProductDetailBinding
import com.example.shopeeclone.model.Product
import com.example.shopeeclone.ui.checkout.CheckoutActivity
import java.text.NumberFormat
import java.util.Locale
import com.example.shopeeclone.R
import com.example.shopeeclone.repository.WishlistRepository

class ProductDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        val product = getProductExtra() ?: run {
            finish()
            return
        }

        val formatter = NumberFormat.getNumberInstance(Locale("th", "TH"))
        binding.imgProduct.setImageResource(product.imageRes)
        binding.tvName.text = product.name
        binding.tvPrice.text = "฿${formatter.format(product.price)}"
        binding.tvDescription.text = product.description
        val updateHeart = {
            binding.ivWishlist.setImageResource(
                if (WishlistRepository.isWishlisted(this, product.id)) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )
        }
        updateHeart()
        binding.ivWishlist.setOnClickListener {
            WishlistRepository.toggle(this, product.id)
            updateHeart()
        }

        binding.btnBuyNow.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            intent.putExtra(CheckoutActivity.EXTRA_PRODUCT, product)
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    private fun getProductExtra(): Product? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_PRODUCT, Product::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_PRODUCT) as? Product
        }
    }
}
