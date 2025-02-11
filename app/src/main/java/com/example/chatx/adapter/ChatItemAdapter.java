package com.example.chatx.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatx.ChatActivity;
import com.example.chatx.R;
import com.example.chatx.models.ChatItem;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChatItemAdapter extends RecyclerView.Adapter<ChatItemAdapter.ViewHolder> {
    private List<ChatItem> chatItems;

    public ChatItemAdapter(List<ChatItem> chatItems) {
        this.chatItems = chatItems;
    }

    public void updateItems(List<ChatItem> newItems) {
        this.chatItems = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatItem item = chatItems.get(position);
        holder.name.setText(item.getName());
        holder.lastMessage.setText(item.getLastMessage());
        
        if (item.getImage() != null && !item.getImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImage())
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.default_profile_image);
        }

        // Set online status if available
        if (holder.onlineStatus != null) {
            holder.onlineStatus.setVisibility(item.isOnline() ? View.VISIBLE : View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChatActivity.class);
            intent.putExtra("userId", item.getId());
            intent.putExtra("userName", item.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ShapeableImageView image;
        public final TextView name;
        public final TextView lastMessage;
        public final View onlineStatus;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.avatarImage);
            name = view.findViewById(R.id.nameText);
            lastMessage = view.findViewById(R.id.lastMessageText);
            onlineStatus = view.findViewById(R.id.onlineStatus);
        }
    }
}
