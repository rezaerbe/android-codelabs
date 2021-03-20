package com.erbe.shrine.productgrid

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.erbe.shrine.R
import com.erbe.shrine.network.ImageRequester
import com.erbe.shrine.network.ProductEntry

/**
 * Adapter used to show a simple grid of products.
 */
class ProductCardRecyclerViewAdapter internal constructor(private val productList: List<ProductEntry>) :
    RecyclerView.Adapter<ProductCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCardViewHolder {
        val layoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.shr_product_card, parent, false)
        return ProductCardViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ProductCardViewHolder, position: Int) {
        if (position < productList.size) {
            val product = productList[position]
            holder.productTitle.text = product.title
            holder.productPrice.text = product.price
            ImageRequester.setImageFromUrl(holder.productImage, product.url)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}