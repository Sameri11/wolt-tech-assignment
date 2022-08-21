package com.mikhailn.wolt_assignment.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikhailn.wolt_assignment.R
import com.mikhailn.wolt_assignment.data.domain.Restaurant
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private class RestaurantListAdapter(
    private val onFavoriteClick: (Restaurant, ImageButton) -> Unit
) : ListAdapter<Restaurant, RestaurantListAdapter.ViewHolder>(RestaurantDiffUtilCallback()) {

    class RestaurantDiffUtilCallback : DiffUtil.ItemCallback<Restaurant>() {
        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            return oldItem.isFavorite == newItem.isFavorite
                    && oldItem.name == newItem.name
                    && oldItem.imageUrl == newItem.imageUrl
                    && oldItem.shortDescription == newItem.shortDescription
        }

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val restaurantImageView: ImageView
        val restaurantNameView: TextView
        val restaurantDescriptionView: TextView
        val restaurantFavoritesButton: ImageButton

        init {
            restaurantImageView = view.findViewById(R.id.restaurant_pic)
            restaurantNameView = view.findViewById(R.id.restaurant_name)
            restaurantDescriptionView = view.findViewById(R.id.restaurant_description)
            restaurantFavoritesButton = view.findViewById(R.id.restaurant_favorites_button)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        currentList[position].apply {
            Picasso.get()
                .load(imageUrl)
                .into(holder.restaurantImageView)

            holder.restaurantNameView.text = name
            holder.restaurantDescriptionView.text = shortDescription

            holder.restaurantFavoritesButton.let { imgButton ->
                imgButton.isSelected = isFavorite
                imgButton.setOnClickListener {
                    onFavoriteClick(this, imgButton)
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var restaurantAdapter: RestaurantListAdapter
    private lateinit var restaurantRecycler: RecyclerView
    private lateinit var restaurantLoader: ProgressBar
    private lateinit var restaurantError: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        restaurantRecycler = root.findViewById(R.id.restaurant_list_view)
        restaurantLoader = root.findViewById(R.id.restaurant_loader)
        restaurantError = root.findViewById(R.id.restaurant_error)

        restaurantAdapter = RestaurantListAdapter(this@MainFragment::onFavoriteClicked)
        restaurantRecycler.adapter = restaurantAdapter

        return root
    }

    override fun onResume() {
        super.onResume()

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.LOADED -> {
                    restaurantLoader.visibility = GONE
                    restaurantError.visibility = GONE
                    restaurantRecycler.visibility = VISIBLE
                }
                UiState.LOADING -> {
                    restaurantRecycler.visibility = GONE
                    restaurantError.visibility = GONE
                    restaurantLoader.visibility = VISIBLE
                }
                UiState.ERROR -> {
                    restaurantRecycler.visibility = GONE
                    restaurantLoader.visibility = GONE
                    restaurantError.visibility = VISIBLE
                }
            }
        }

        viewModel.restaurants.observe(viewLifecycleOwner) {
            restaurantAdapter.apply {
                submitList(it)
            }
        }

        viewModel.currentPlace.observe(viewLifecycleOwner) {
            lifecycleScope.launch {
                viewModel.updateRestaurants(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.removeObservables(viewLifecycleOwner)
    }

    private fun onFavoriteClicked(restaurant: Restaurant, button: ImageButton) {
        button.isSelected = !restaurant.isFavorite
        if (restaurant.isFavorite) {
            viewModel.removeFromFavorites(restaurant)
        } else {
            viewModel.addToFavorites(restaurant)
        }
    }
}
