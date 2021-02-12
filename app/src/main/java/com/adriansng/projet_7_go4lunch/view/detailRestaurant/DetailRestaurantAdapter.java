package com.adriansng.projet_7_go4lunch.view.detailRestaurant;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class DetailRestaurantAdapter extends RecyclerView.Adapter<DetailRestaurantAdapter.WorkmateViewHolder> {

    // --- FOR DATA ---

    private final List<Workmate> mWorkmateList;
    private Context context;


    public DetailRestaurantAdapter(List<Workmate> workmateList) {
        this.mWorkmateList = workmateList;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.detail_restaurant_workmate_item, parent, false);
        context = view.getContext();
        return new WorkmateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        Workmate workmate = mWorkmateList.get(position);
        String actionWorkmate = context.getString(R.string.detail_adapter_workmate);
        if (!workmate.getAvatar().isEmpty()) {
            Glide.with(Objects.requireNonNull(holder.imageView).getContext())
                    .load(workmate.getAvatar())
                    .circleCrop()
                    .into(holder.imageView);
        }

        holder.textView.setText(String.format("%s%s", workmate.getNameWorkmate(), actionWorkmate));
    }

    @Override
    public int getItemCount() {
        if (mWorkmateList != null) {
            return mWorkmateList.size();
        } else {
            return 0;
        }
    }


    public static class WorkmateViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        public WorkmateViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_workmate_list);
            textView = itemView.findViewById(R.id.name_workmate_list);
        }
    }
}
