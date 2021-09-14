package kovacs.chatlicenseapp.manage.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jsibbold.zoomage.ZoomageView;

import java.util.Objects;

import kovacs.chatlicenseapp.R;

public class DialogReviewSendImage {
    private Context context;
    private Bitmap bitmap;

    private Dialog dialog;
    private ProgressDialog progressDialog;

    private ZoomageView image;
    private FloatingActionButton sendButton;

    public DialogReviewSendImage(Context context, Bitmap bitmap) {
        this.context = context;
        this.bitmap = bitmap;
        this.dialog = new Dialog(context);
        init();
    }

    public void init() {
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR);
        dialog.setContentView(R.layout.activity_review_chat_image);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(dialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);

        image = dialog.findViewById(R.id.review_image);
        sendButton = dialog.findViewById(R.id.send_button);
    }

    public void show(final onCallBack onCallBack) {
        dialog.show();
        image.setImageBitmap(bitmap);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCallBack.onButtonSendClick();
                dialog.dismiss();
            }
        });
    }

    public interface onCallBack {
        void onButtonSendClick();
    }
}
