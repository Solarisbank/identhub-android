package de.solarisbank.sdk.fourthline.feature.ui.intro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.solarisbank.identhub.fourthline.R
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.customization.ImageViewTint
import de.solarisbank.sdk.feature.customization.customize

class SlideAdapter(private val context: Context,
                   private val slides: List<Slide>,
                   private val customization: Customization) :
    RecyclerView.Adapter<SlideAdapter.SlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.identhub_fourthline_intro_slide_item, parent, false)
        return SlideViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val slide = slides[position]
        holder.bind(slide)
    }

    override fun getItemCount(): Int {
        return slides.size
    }

    inner class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(slide: Slide) {
            val sliderImageBackground = itemView.findViewById<ImageView>(R.id.sliderImageBackground)
            sliderImageBackground.customize(customization, ImageViewTint.Primary)
            itemView.findViewById<ImageView>(R.id.sliderImage).setImageResource(slide.image)
            itemView.findViewById<TextView>(R.id.sliderTitle).text = context.getString(slide.title)
            itemView.findViewById<TextView>(R.id.sliderDescription).text = context.getString(slide.description)
        }
    }
}

data class Slide(val image: Int, val title: Int, val description: Int)
