package kovacs.chatlicenseapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import kovacs.chatlicenseapp.R;
import kovacs.chatlicenseapp.model.Chat;
import kovacs.chatlicenseapp.services.AudioServices;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Chat> chatList;
    private Context context;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private FirebaseUser firebaseUser;

    private ImageButton tmpButtonPlay;
    private AudioServices audioServices;

    public ChatAdapter(List<Chat> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        this.audioServices = new AudioServices(context);
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_item, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        holder.bind(chatList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textMessage, textDate, imageDate, vocalDate;
        private LinearLayout textLayout, imageLayout, vocalLayout;
        private ImageButton playVocalButton;
        private ImageView chatImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.tv_text_message);
            textDate = itemView.findViewById(R.id.tv_textDate);
            imageDate = itemView.findViewById(R.id.tv_imageDate);
            vocalDate = itemView.findViewById(R.id.tv_vocalDate);

            textLayout = itemView.findViewById(R.id.text_layout);
            imageLayout = itemView.findViewById(R.id.image_layout);
            vocalLayout = itemView.findViewById(R.id.vocal_layout);
            chatImage = itemView.findViewById(R.id.chat_image);
            playVocalButton = itemView.findViewById(R.id.button_play_voice_chat);
        }

        void bind(Chat chat) {
            // Check type of message
            switch (chat.getType()) {
                case "text":
                    textLayout.setVisibility(View.VISIBLE);
                    imageLayout.setVisibility(View.GONE);
                    vocalLayout.setVisibility(View.GONE);

                    textMessage.setText(chat.getTextMessage());
                    textDate.setText(chat.getDatetime());
                    break;

                case "image":
                    textLayout.setVisibility(View.GONE);
                    imageLayout.setVisibility(View.VISIBLE);
                    vocalLayout.setVisibility(View.GONE);

                    Glide.with(context).load(chat.getUrl()).into(chatImage);
                    imageDate.setText(chat.getDatetime());
                    break;

                case "vocal":
                    textLayout.setVisibility(View.GONE);
                    imageLayout.setVisibility(View.GONE);
                    vocalLayout.setVisibility(View.VISIBLE);

                    vocalLayout.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("UseCompatLoadingForDrawables")
                        @Override
                        public void onClick(View v) {
                            if (tmpButtonPlay != null) {
                                playVocalButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                            }

                            playVocalButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_pause_circle_24));

                            audioServices.playAudioFromUrl(chat.getUrl(), new AudioServices.OnPlayCallBack() {
                                @Override
                                public void onFinished() {
                                    playVocalButton.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_play_circle_24));
                                }
                            });

                            tmpButtonPlay = playVocalButton;
                        }
                    });

                    vocalDate.setText(chat.getDatetime());
                    break;
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
