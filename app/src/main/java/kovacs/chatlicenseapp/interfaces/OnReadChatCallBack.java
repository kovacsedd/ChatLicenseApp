package kovacs.chatlicenseapp.interfaces;

import java.util.List;

import kovacs.chatlicenseapp.model.Chat;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chat> list);
    void onReadFailed();
}
