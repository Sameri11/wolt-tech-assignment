package com.mikhailn.wolt_assignment.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mikhailn.wolt_assignment.R
import com.mikhailn.wolt_assignment.data.domain.Restaurant
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private class RestaurantListAdapter(
    context: Context,
    layout: Int,
    restaurants: List<Restaurant>,
    private val onFavoriteClick: (Restaurant, ImageButton) -> Unit
) : ArrayAdapter<Restaurant>(context, layout, restaurants) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.restaurant_card, parent, false)
        val restaurant = getItem(position) ?: return view

        val restaurantPicImageView = view.findViewById<ImageView>(R.id.restaurant_pic)
        val restaurantNameTextView = view.findViewById<TextView>(R.id.restaurant_name)
        val restaurantDescriptionTextView = view.findViewById<TextView>(R.id.restaurant_description)

        view.findViewById<ImageButton>(R.id.restaurant_favorites_button).apply {
            isSelected = restaurant.isFavorite
            setOnClickListener {
                onFavoriteClick(restaurant, this)
            }
        }

        restaurantNameTextView.text = restaurant.name
        restaurantDescriptionTextView.text = restaurant.shortDescription
        if (convertView == null) {
            Picasso.get()
                .load(restaurant.imageUrl)
                .into(restaurantPicImageView);
        }

        return view
    }
}

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()
    private lateinit var restaurantListView: ListView
    private lateinit var restaurantLoader: ProgressBar
    private lateinit var restaurantError: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.main_fragment, container, false)
        restaurantListView = root.findViewById(R.id.restaurant_list_view)
        restaurantLoader = root.findViewById(R.id.restaurant_loader)
        restaurantError = root.findViewById(R.id.restaurant_error)

        return root
    }

    override fun onResume() {
        super.onResume()

        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                UiState.LOADED -> {
                    restaurantLoader.visibility = GONE
                    restaurantError.visibility = GONE
                    restaurantListView.visibility = VISIBLE
                }
                UiState.LOADING -> {
                    restaurantListView.visibility = GONE
                    restaurantError.visibility = GONE
                    restaurantLoader.visibility = VISIBLE
                }
                UiState.ERROR -> {
                    restaurantListView.visibility = GONE
                    restaurantLoader.visibility = GONE
                    restaurantError.visibility = VISIBLE
                }
            }
        }

        viewModel.restaurants.observe(viewLifecycleOwner) {
            restaurantListView.findViewById<ListView>(R.id.restaurant_list_view).apply {
                adapter = RestaurantListAdapter(context, R.layout.restaurant_card, it, this@MainFragment::onFavoriteClicked)
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
