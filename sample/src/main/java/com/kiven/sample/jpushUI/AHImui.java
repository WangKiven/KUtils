package com.kiven.sample.jpushUI;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.kiven.kutils.activityHelper.KActivityDebugHelper;
import com.kiven.kutils.activityHelper.KHelperActivity;
import com.kiven.kutils.tools.KUtil;
import com.kiven.sample.R;

import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cn.jiguang.imui.chatinput.ChatInputView;
import cn.jiguang.imui.chatinput.listener.OnMenuClickListener;
import cn.jiguang.imui.chatinput.listener.RecordVoiceListener;
import cn.jiguang.imui.chatinput.menu.Menu;
import cn.jiguang.imui.chatinput.menu.MenuManager;
import cn.jiguang.imui.chatinput.model.FileItem;
import cn.jiguang.imui.chatinput.record.RecordVoiceButton;
import cn.jiguang.imui.commons.ImageLoader;
import cn.jiguang.imui.commons.models.IMessage;
import cn.jiguang.imui.commons.models.IUser;
import cn.jiguang.imui.messages.MessageList;
import cn.jiguang.imui.messages.MsgListAdapter;

public class AHImui extends KActivityDebugHelper {

    private String toAccount;

    private int sessionType = 0;


    @Override
    public void onCreate(KHelperActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        setContentView(R.layout.ah_imui);

        toAccount = mActivity.getIntent().getStringExtra("toAccount");
        sessionType = mActivity.getIntent().getIntExtra("sessionType", 0);

        initList();

        ChatInputView chatInputView = (ChatInputView) findViewById(R.id.chat_input);
        chatInputView.setMenuContainerHeight(KUtil.dip2px(260));

        // add Custom Menu View
        MenuManager menuManager = chatInputView.getMenuManager();
//        menuManager.addCustomMenu("MY_CUSTOM",R.layout.menu_text_item,R.layout.menu_text_feature);

        // Custom menu order
        menuManager.setMenu(Menu.newBuilder().
                customize(true).
                setLeft(Menu.TAG_VOICE).
                setRight(Menu.TAG_SEND, Menu.TAG_EMOJI).
//                setBottom(Menu.TAG_VOICE, Menu.TAG_EMOJI, Menu.TAG_GALLERY/*, Menu.TAG_CAMERA,"MY_CUSTOM"*/).
        build());
        /*menuManager.setCustomMenuClickListener(new CustomMenuEventListener() {
            @Override
            public boolean onMenuItemClick(String tag, MenuItem menuItem) {
                //Menu feature will not be show shown if return false；
                return false;
            }

            @Override
            public void onMenuFeatureVisibilityChanged(int visibility, String tag, MenuFeature menuFeature) {
                if (visibility == View.VISIBLE) {
                    // Menu feature is visible.
                } else {
                    // Menu feature is gone.
                }
            }
        });*/


        chatInputView.setMenuClickListener(new OnMenuClickListener() {
            @Override
            public boolean onSendTextMessage(CharSequence input) {
                // 以单聊类型为例
//                SessionTypeEnum sessionType = new SessionTypeEnum(sessionType);

                /*UserBo userBo = AppContext.getInstance().getUser();

                final MyMessage message = new MyMessage(input.toString(), IMessage.MessageType.SEND_TEXT.ordinal());
                message.setUserInfo(new DefaultUser("" + userBo.getUserID(), userBo.getUserName(), userBo.getUserHead()));
                message.status = IMessage.MessageStatus.SEND_GOING;
                message.setDuration(System.currentTimeMillis());

                sendMsg(message);

                adapter.addToStart(message, true);*/
                return true;
            }

            @Override
            public void onSendFiles(List<FileItem> list) {

            }

            @Override
            public boolean switchToMicrophoneMode() {
                return true;
            }

            @Override
            public boolean switchToGalleryMode() {
                /*BusinessUtils.createImage(mActivity);*/
                return false;
            }

            @Override
            public boolean switchToCameraMode() {
                return false;
            }

            @Override
            public boolean switchToEmojiMode() {
                return true;
            }
        });
        final RecordVoiceButton mRecordVoiceBtn = chatInputView.getRecordVoiceButton();
        mRecordVoiceBtn.setRecordVoiceListener(new RecordVoiceListener() {
            @Override
            public void onStartRecord() {
                File vf = mActivity.getDir(Environment.DIRECTORY_MOVIES, Context.MODE_PRIVATE);
                if (!vf.exists()) {
                    vf.mkdir();
                }
                mRecordVoiceBtn.setVoiceFilePath(vf.getAbsolutePath(),
                        "" + System.currentTimeMillis());
            }

            @Override
            public void onFinishRecord(File voiceFile, int duration) {

            }

            @Override
            public void onCancelRecord() {

            }

            @Override
            public void onPreviewCancel() {

            }

            @Override
            public void onPreviewSend() {

            }
        });

        receiveMessage();
    }

    private void receiveMessage() {
        /*NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessage, true);*/
    }

    /*Observer<List<IMMessage>> receiveMessage = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> imMessages) {
            for (IMMessage imm : imMessages) {
                if (TextUtils.equals(imm.getFromAccount(), toAccount)) {
                    adapter.addToStart(new MyMessage(imm), false);
                }
            }
        }
    };*/

    /*@Override
    public void onDestroy() {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(receiveMessage, false);
        super.onDestroy();
    }*/

    private void sendMsg(final MyMessage message) {
        /*IMMessage nim = message.createSendMessage();

        NIMClient.getService(MsgService.class)
                .sendMessage(nim, false)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        message.status = IMessage.MessageStatus.SEND_SUCCEED;
                        adapter.updateMessage(message);
                    }

                    @Override
                    public void onFailed(int i) {
                        message.status = IMessage.MessageStatus.SEND_FAILED;
                        adapter.updateMessage(message);
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });*/
    }

    MsgListAdapter adapter = new MsgListAdapter("0", new ImageLoader() {
        @Override
        public void loadAvatarImage(ImageView avatarImageView, String string) {

        }

        @Override
        public void loadImage(ImageView imageView, String string) {

        }

        @Override
        public void loadVideo(ImageView imageCover, String uri) {

        }
    });

    private void initList() {
        adapter.setMsgStatusViewClickListener(new MsgListAdapter.OnMsgStatusViewClickListener() {
            @Override
            public void onStatusViewClick(IMessage message) {
                adapter.updateMessage(message);
                sendMsg((MyMessage) message);
            }
        });

        MessageList messageList = findViewById(R.id.messageList);
        messageList.setAdapter(adapter);
    }

    public class MyMessage implements IMessage {

        private long id;
        private String text;
        private String timeString;
        private int type;
        private IUser user;
        private String contentFile;
        private long duration;
        private MessageStatus status;

        public MyMessage(String text, int type) {
            this.text = text;
            this.type = type;
            this.id = UUID.randomUUID().getLeastSignificantBits();
        }

        /*public MyMessage(IMMessage imm) {
            user = new DefaultUser(toAccount, imm.getFromNick(), "http://a.hiphotos.baidu.com/image/pic/item/4a36acaf2edda3ccc4a53e450ce93901213f9216.jpg");
            switch (imm.getMsgType()) {
                case text:
                    this.text = imm.getContent();
                    this.type = MessageType.RECEIVE_TEXT.ordinal();
                    break;
                case image:
                    this.contentFile = imm.getContent();
                    this.type = MessageType.RECEIVE_IMAGE.ordinal();
                    break;
                case audio:
                    this.contentFile = imm.getContent();
                    this.type = MessageType.RECEIVE_VOICE.ordinal();
                    break;
            }

            this.duration = imm.getTime();

            boolean isMySend = !TextUtils.equals(toAccount, imm.getFromAccount());
            if (isMySend) {
                switch (imm.getStatus()) {
                    case fail:
                        this.status = MessageStatus.SEND_FAILED;
                        break;
                    case sending:
                        this.status = MessageStatus.SEND_GOING;
                        break;
                    case draft:
                        this.status = MessageStatus.SEND_DRAFT;
                        break;
                    default:
                        this.status = MessageStatus.SEND_SUCCEED;
                        break;
                }
            } else {
                this.status = MessageStatus.RECEIVE_SUCCEED;
            }
            this.id = UUID.randomUUID().getLeastSignificantBits();
        }*/

        @Override
        public String getMsgId() {
            return String.valueOf(id);
        }

        @Override
        public IUser getFromUser() {
            if (user == null) {
                return new DefaultUser("0", "", null);
            }
            return user;
        }

        public void setUserInfo(IUser user) {
            this.user = user;
        }

        public void setMediaFilePath(String path) {
            this.contentFile = path;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        @Override
        public long getDuration() {
            return duration;
        }

        @Override
        public String getProgress() {
            return null;
        }

        @Override
        public HashMap<String, String> getExtras() {
            return null;
        }

        public void setTimeString(String timeString) {
            this.timeString = timeString;
        }

        @Override
        public String getTimeString() {
            return timeString;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public MessageStatus getMessageStatus() {
            return status;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public String getMediaFilePath() {
            return contentFile;
        }

        /*public IMMessage createSendMessage() {
            switch (type) {
                case 1:
                    return MessageBuilder.createTextMessage(toAccount, sessionType, text);
                case 3:
                    return MessageBuilder.createImageMessage(toAccount, sessionType, new File(contentFile));
                case 5:
                    return MessageBuilder.createAudioMessage(toAccount, sessionType, new File(contentFile), 5000);
                default:
                    return null;
            }
        }*/
    }

    public class DefaultUser implements IUser {

        private String id;
        private String displayName;
        private String avatar;

        public DefaultUser(String id, String displayName, String avatar) {
            this.id = id;
            this.displayName = displayName;
            this.avatar = avatar;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String getAvatarFilePath() {
            return avatar;
        }
    }
}
