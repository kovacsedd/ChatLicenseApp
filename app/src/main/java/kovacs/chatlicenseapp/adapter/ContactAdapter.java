package kovacs.chatlicenseapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.chat.ChatActivity;
import kovacs.chatlicenseapp.model.User;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    private List<User> userList;
    private Context context;

    public ContactAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.username.setText(user.getUsername());
        holder.bio.setText(user.getBio());
        //Glide.with(context).load(user.getImageProfile()).into(holder.profileImage);

        if (user.getImageProfile().equals("")) {
            // set default images
            holder.profileImage.setImageResource(R.drawable.ic_placeholder_person);
        } else {
            Glide.with(context).load(user.getImageProfile()).into(holder.profileImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatActivity.class)
                        .putExtra("userID", user.getUserID())
                        .putExtra("username", user.getUsername())
                        .putExtra("imageProfile", user.getImageProfile()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profileImage;
        private TextView username, bio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.contact_profile_image);
            username = itemView.findViewById(R.id.contact_username);
            bio = itemView.findViewById(R.id.contact_bio);
        }
    }
}