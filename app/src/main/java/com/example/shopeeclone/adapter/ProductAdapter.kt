package com.example.shopeeclone.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.shopeeclone.R
import com.example.shopeeclone.databinding.ItemProductBinding
import com.example.shopeeclone.model.Product
import com.example.shopeeclone.repository.WishlistRepository
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: List<Product>,
    private val onWishlistChanged: () -> Unit = {},
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        val ctx = holder.itemView.context
        val formatter = NumberFormat.getNumberInstance(Locale("th", "TH"))

        holder.binding.imgProduct.setImageResource(product.imageRes)
        holder.binding.tvName.text = product.name
        holder.binding.tvPrice.text = "฿${formatter.format(product.price)}"

        val refreshHeart = {
            holder.binding.ivWishlist.setImageResource(
                if (WishlistRepository.isWishlisted(ctx, product.id)) R.drawable.ic_heart_filled
                else R.drawable.ic_heart_outline
            )
        }
        refreshHeart()
        holder.binding.ivWishlist.setOnClickListener {
            WishlistRepository.toggle(ctx, product.id)
            refreshHeart()
            onWishlistChanged()
        }

        holder.binding.root.setOnClickListener { onProductClick(product) }
    }

    override fun getItemCount(): Int = products.size
}