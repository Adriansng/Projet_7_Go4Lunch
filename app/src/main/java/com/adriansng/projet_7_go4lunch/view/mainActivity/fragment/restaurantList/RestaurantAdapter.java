package com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.restaurantList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Restaurant;
import com.adriansng.projet_7_go4lunch.view.detailRestaurant.DetailRestaurantActivity;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>{

    // -- FOR DATA ---

    private final List<Restaurant> listRestaurants;

    // --- ADAPTER INSTANCE ---

    RestaurantAdapter(List<Restaurant> items) {
        this.listRestaurants = items;
    }

    // ------------------
    // TO CREATE
    // ------------------

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.restaurant_list_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    // ------------------
    // ADAPTER
    // ------------------

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = listRestaurants.get(position);
        holder.setData(restaurant, holder);
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetailRestaurantActivity.class);
            intent.putExtra("Restaurant", restaurant);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (listRestaurants != null) {
            return listRestaurants.size();
        } else {
            return 0;
        }
    }

    // ------------------
    // VIEW HOLDER
    // ------------------

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        // --- FOR DATA ---

        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.tv_name_restaurant_list)
        TextView tvNameRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.tv_style_and_address_restaurant_list)
        TextView tvStyleAddressRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.tv_distance_restaurant_list)
        TextView tvDistanceRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.tv_hour_restaurant_list)
        TextView tvHourRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.tv_number_workmate_restaurant_list)
        TextView tvNumberWorkmateRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.iv_restaurant_list)
        ImageView ivAvatarRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.iv_notation_restaurant_list)
        ImageView ivNotationRestaurant;
        @SuppressLint("NonConstantResourceId") @Nullable@BindView(R.id.iv_star_like_list)
        ImageView ivStarLikeRestaurant;

        Restaurant restaurantView;

        // --- VIEW HOLDER INSTANCE ---

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        // --- UI FOR ADAPTER ---

        @SuppressLint("SetTextI18n")
        private void setData(Restaurant restaurant, RestaurantViewHolder holder) {
            this.restaurantView = restaurant;
            Objects.requireNonNull(holder.tvNameRestaurant).setText(restaurant.getName());
            Objects.requireNonNull(holder.tvStyleAddressRestaurant).setText(restaurant.getAddress());
            Objects.requireNonNull(holder.tvDistanceRestaurant).setText(restaurant.getDistance());
            Objects.requireNonNull(holder.tvNumberWorkmateRestaurant).setText("(" + restaurant.getNumberWorkmate() + ")");
            getRating(restaurant, holder);
            getOpenNowRestaurant(restaurant, holder);
            getPhotoRestaurant(restaurant, holder);
            getStarFavorite(restaurant, holder);
        }


        private void getOpenNowRestaurant(Restaurant restaurant, RestaurantViewHolder holder) {
            if ((restaurant.getOpenHours() != null) && restaurant.getOpenHours()) {
                Objects.requireNonNull(holder.tvHourRestaurant).setText(R.string.open_now);
                holder.tvHourRestaurant.setTypeface(null, Typeface.ITALIC);
                holder.tvHourRestaurant.setTextColor(Color.BLACK);
            } else {
                Objects.requireNonNull(holder.tvHourRestaurant).setText(R.string.closing_soon);
                holder.tvHourRestaurant.setTypeface(null, Typeface.BOLD);
                holder.tvHourRestaurant.setTextColor(Color.RED);
            }
        }

        private void getRating(Restaurant restaurant, RestaurantViewHolder holder) {
            double notation = restaurant.getNotation();
            if (holder.ivNotationRestaurant != null) {
                if (notation >= 3) {
                    holder.ivNotationRestaurant.setImageResource(R.drawable.ic_star_three);
                } else if (notation >= 2) {
                    holder.ivNotationRestaurant.setImageResource(R.drawable.ic_star_two);
                } else if (notation >= 1) {
                    holder.ivNotationRestaurant.setImageResource(R.drawable.ic_star);
                } else if (notation >= 0) {
                    holder.ivNotationRestaurant.setVisibility(View.INVISIBLE);
                }
            }
        }

        private void getPhotoRestaurant(Restaurant restaurant, RestaurantViewHolder holder) {
            if (!restaurant.getPhoto().isEmpty()) {
                Glide.with(Objects.requireNonNull(holder.ivAvatarRestaurant)
                        .getContext())
                        .load(restaurant.getPhoto())
                        .centerCrop()
                        .into(holder.ivAvatarRestaurant);
            }
        }

        private void getStarFavorite(Restaurant restaurant, RestaurantViewHolder holder) {
            if (!restaurant.isFavorite()) {
                Objects.requireNonNull(holder.ivStarLikeRestaurant).setVisibility(View.INVISIBLE);
            } else {
                Objects.requireNonNull(holder.ivStarLikeRestaurant).setVisibility(View.VISIBLE);
            }
        }
    }
}
