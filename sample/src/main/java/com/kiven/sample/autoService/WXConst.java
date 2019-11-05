package com.kiven.sample.autoService;

import java.util.TreeMap;

public class WXConst {
    public class Page {
        public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";//微信 界面
        public static final String SnsTimeLineUI = "com.tencent.mm.plugin.sns.ui.SnsTimeLineUI";//朋友圈 界面
        public static final String settingUI = "com.tencent.mm.plugin.setting.ui.setting.SettingsUI";//设置 界面
        public static final String tongYongSettingUI = "com.tencent.mm.plugin.setting.ui.setting.SettingsAboutSystemUI";//设置->通用 界面
        public static final String SettingsPluginsUI = "com.tencent.mm.plugin.setting.ui.setting.SettingsPluginsUI";//设置->通用->辅助功能 界面
        public static final String ContactInfoUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";//设置->通用->辅助功能->群发助手 界面
        public static final String MassSendHistoryUI = "com.tencent.mm.plugin.masssend.ui.MassSendHistoryUI";//设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面
        //设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面
        public static final String MassSendSelectContactUI = "com.tencent.mm.plugin.masssend.ui.MassSendSelectContactUI";
        //设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->点击搜索框弹出的标签进入的'按标签选择界面'
        public static final String SelectLabelContactUI = "com.tencent.mm.ui.contact.SelectLabelContactUI";
        //设置->通用->辅助功能->群发助手->点击'开始群发'出现的有'新建群发'按钮的界面->选择收信人界面->群发消息输入界面
        // 这个界面点击发送后，回到'MassSendHistoryUI'界面
        public static final String MassSendMsgUI = "com.tencent.mm.plugin.masssend.ui.MassSendMsgUI";
        // 媒体文件选择界面
        public static final String AlbumPreviewUI = "com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI";
        // 图片剪切界面
        public static final String CropImageNewUI = "com.tencent.mm.ui.tools.CropImageNewUI";

        // 标签界面：微信主界面通讯录中点击'标签'按钮进入
        public static final String ContactLabelManagerUI = "com.tencent.mm.plugin.label.ui.ContactLabelManagerUI";
    }

    public static int logType = 2;//控制打印日志, 微信工具用
    public static final TreeMap<String, Integer> frindsTags = new TreeMap<>();
}
