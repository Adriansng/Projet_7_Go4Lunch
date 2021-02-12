package com.adriansng.projet_7_go4lunch.view.mainActivity.fragment.workmates;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.view.chatWorkmate.ChatActivity;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Objects;

public class WorkmatesListAdapter extends RecyclerView.Adapter<WorkmatesListAdapter.WorkmateViewHolder> {

    private final List<Workmate> mWorkmateList;
    private Context context;

    public WorkmatesListAdapter(List<Workmate> workmateList) {
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
        String actionWorkmate;
        if (!workmate.getAvatar().isEmpty()) {
            Glide.with(Objects.requireNonNull(holder.imageView).getContext())
                    .load(workmate.getAvatar())
                    .circleCrop()
                    .into(holder.imageView);
        }

        if (workmate.getChooseRestaurant() != null && !workmate.getChooseRestaurant().isEmpty()) {
            actionWorkmate = context.getString(R.string.workmate_adapter_eating) + workmate.getNameChooseRestaurant();
        } else {
            actionWorkmate = context.getString(R.string.workmate_adapter_not_decided);
            holder.textView.setTextColor(Color.GRAY);
        }

        holder.textView.setText(String.format("%s%s", workmate.getNameWorkmate(), actionWorkmate));
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("Workmate", workmate);
            context.startActivity(intent);
        });

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
