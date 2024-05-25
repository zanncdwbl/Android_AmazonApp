package com.example.projectworkpcparts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import android.content.Intent
import android.net.Uri


class ProductAdapter(private var matchedProducts: List<ProductMatched>, context: Context) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImageView: ImageView = itemView.findViewById(R.id.productImageView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val PriceUKTextView: TextView = itemView.findViewById(R.id.PriceUKTextView)
        val UKUrlTextView: TextView = itemView.findViewById(R.id.UKUrlTextView)
//        val fullPriceUKTextView: TextView = itemView.findViewById(R.id.fullPriceUKTextView)
        val PriceDETextView: TextView = itemView.findViewById(R.id.PriceDETextView)
        val DEUrlTextView: TextView = itemView.findViewById(R.id.DEUrlTextView)
//        val fullPriceDETextView: TextView = itemView.findViewById(R.id.fullPriceDETextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_view, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = matchedProducts[position]

        // Load image
        Glide.with(holder.itemView.context)
            .load(product.productImage)
            .into(holder.productImageView)

        holder.titleTextView.text = product.productName

        holder.UKUrlTextView.setOnClickListener {
            openUrl(holder.itemView.context, product.URL_UK)
        }

        holder.DEUrlTextView.setOnClickListener {
            openUrl(holder.itemView.context, product.URL_DE)
        }

        holder.PriceUKTextView.text = "UK : " + (product.salePriceUK ?: product.fullPriceUK ?: "No Data")
        holder.PriceDETextView.text = "DE : " + (product.salePriceDE ?: product.fullPriceDE ?: "No Data")

    }

    private fun openUrl(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    override fun getItemCount(): Int  = matchedProducts.size

    fun refreshData(newProducts: List<ProductMatched>) {
        matchedProducts = newProducts
        notifyDataSetChanged()
    }
}
