package com.adriansng.projet_7_go4lunch.view.chatWorkmate;

import android.annotation.SuppressLint;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Message;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    //ROOT VIEW
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.activity_mentor_chat_item_root_view)
    RelativeLayout rootView;

    //PROFILE CONTAINER
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_profile_container)
    LinearLayout profileContainer;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_profile_container_image)
    ImageView imageViewProfile;

    //MESSAGE CONTAINER
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container)
    RelativeLayout messageContainer;
    //IMAGE SENDED CONTAINER
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container_image_sent_card_view)
    CardView cardViewImageSent;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container_image_sent_card_view_image)
    ImageView imageViewSent;
    //TEXT MESSAGE CONTAINER
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container_txt_message_container)
    LinearLayout textMessageContainer;
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container_txt_message_container_txt)
    TextView textViewMessage;
    //DATE TEXT
    @SuppressLint("NonConstantResourceId") @Nullable @BindView(R.id.chat_item_message_container_txt_date)
    TextView textViewDate;

    //FOR DATA
    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
    }

    public void updateWithMessage(Message message, String currentUserId, RequestManager glide) {
        // Check if current user is the sender
        Boolean isCurrentUser = message.getWorkmateSender().getUid().equals(currentUserId);
        // Update message TextView
        Objects.requireNonNull(this.textViewMessage).setText(message.getMessage());
        this.textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
        // Update date TextView
        if (message.getDateCreated() != null)
            Objects.requireNonNull(this.textViewDate).setText(this.convertDateToHour(message.getDateCreated()));
        // Update profile picture ImageView
        if (message.getWorkmateSender().getAvatar() != null)
            glide.load(message.getWorkmateSender().getAvatar())
                    .apply(RequestOptions.circleCropTransform())
                    .into(Objects.requireNonNull(imageViewProfile));
        // Update image sent ImageView
        if (message.getUrlImage() != null) {
            glide.load(message.getUrlImage())
                    .into(Objects.requireNonNull(imageViewSent));
            this.imageViewSent.setVisibility(View.VISIBLE);
        } else {
            Objects.requireNonNull(this.imageViewSent).setVisibility(View.GONE);
        }
        //Update Message Bubble Color Background
        ((GradientDrawable) Objects.requireNonNull(textMessageContainer).getBackground()).setColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);
        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser);
    }

    private void updateDesignDependingUser(Boolean isSender) {
        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        Objects.requireNonNull(this.profileContainer).setLayoutParams(paramsLayoutHeader);
        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.chat_item_profile_container);
        Objects.requireNonNull(this.messageContainer).setLayoutParams(paramsLayoutContent);
        // CARD VIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.chat_item_message_container_txt_message_container);
        Objects.requireNonNull(this.cardViewImageSent).setLayoutParams(paramsImageView);
        Objects.requireNonNull(this.rootView).requestLayout();
    }

    private String convertDateToHour(Date date) {
        @SuppressLint("SimpleDateFormat") DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }
}
