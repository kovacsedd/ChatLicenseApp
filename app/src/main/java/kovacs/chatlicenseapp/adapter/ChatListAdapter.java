package kovacs.chatlicenseapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.chat.ChatActivity;
import kovacs.chatlicenseapp.model.ChatList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.Holder> {
    private List<ChatList> list;
    private Context context;

    public ChatListAdapter(List<ChatList> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_list_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ChatList chatList = list.get(position);

        holder.tvName.setText(chatList.getUserName());

        if (chatList.getUrlProfile().equals("")) {
            // set default images
            holder.tvProfileImage.setImageResource(R.drawable.ic_placeholder_person);
        } else {
            Glide.with(context).load(chatList.getUrlProfile()).into(holder.tvProfileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", chatList.getUserID())
                        .putExtra("username", chatList.getUserName())
                        .putExtra("imageProfile", chatList.getUrlProfile()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private CircularImageView tvProfileImage;

        public Holder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvProfileImage = itemView.findViewById(R.id.tv_profile_image);
        }
    }
}
