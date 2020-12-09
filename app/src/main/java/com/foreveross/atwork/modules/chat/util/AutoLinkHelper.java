package com.foreveross.atwork.modules.chat.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.TextChatMessage;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.app.route.UrlRouteHelper;
import com.foreveross.atwork.modules.chat.component.PhoneEmailPopupWindow;
import com.foreveross.atwork.utils.AtworkToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.collections.CollectionsKt;

import static com.foreveross.atwork.infrastructure.utils.PatternUtils.*;


/**
 * 自动识别网址、邮箱、电话号码处理类
 */
public class AutoLinkHelper {


    private static AutoLinkHelper autoLinkHelper = new AutoLinkHelper();

    private static AutoLinkHelper atAutoLinkHelper = new AutoLinkHelper();
    private boolean isLongClick = false;
    private boolean hasAT = false;

    /**用于识别是否为分享*/
    private String fileStr = "";
    /**用于识别是否为分享*/
    private String webStr = null;

    private AutoLinkHelper() {
    }

    public static AutoLinkHelper getAtInstance() {
        atAutoLinkHelper.hasAT = true;
        return atAutoLinkHelper;
    }

    public static AutoLinkHelper getInstance() {
        return autoLinkHelper;
    }

    public void setLongClick(boolean isLongClick) {
        this.isLongClick = isLongClick;
    }

    public SpannableString getSpannableString(Context context, String sessionId, ChatPostMessage textChatMessage, TextView textView, String text) {
        if (context == null || textView == null || text == null) {
            return null;
        }
        SpannableString spannableString = new SpannableString(text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        filterWebUrlEmailPhone(context, spannableString, sessionId, textChatMessage);

        if (textChatMessage instanceof TextChatMessage) {
            filterAtAll(context, (TextChatMessage) textChatMessage, spannableString);
        }

        return spannableString;
    }

    public SpannableString getTextMessageSpannableString(Context context, TextView textView, String sessionId, TextChatMessage textChatMessage) {
        if (context == null || textView == null || textChatMessage.text == null) {
            return null;
        }
        SpannableString spannableString = new SpannableString(textChatMessage.text);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        filterTextRouteLinkSpannableString(context, textView, sessionId, textChatMessage, spannableString);

        filterWebUrlEmailPhone(context, spannableString, sessionId, textChatMessage);

        filterAtAll(context, textChatMessage, spannableString);

        return spannableString;
    }

    private void filterAtAll(Context context, TextChatMessage textChatMessage, SpannableString spannableString) {
        if(textChatMessage.atAll) {
            int color;
            if(User.isYou(AtworkApplicationLike.baseContext, textChatMessage.from)) {
                color = context.getResources().getColor(R.color.self_link);
            } else {
                color = context.getResources().getColor(R.color.peer_link);
            }


            String allAllLabel = StringUtils.EMPTY;

            String content = spannableString.toString();
            if(content.contains("@全部人员")) {
                allAllLabel = "@全部人员";
            } else if(textChatMessage.text.contains("@All")) {
                allAllLabel = "@All";
            } else if(textChatMessage.text.contains("@全部人員")){
                allAllLabel = "@全部人員";
            }

            if(!StringUtils.isEmpty(allAllLabel)) {
//                TextClickableSpan span = new TextClickableSpan(context, sessionId, textChatMessage.deliveryId, allAllLabel, AT);
                int start = content.indexOf(allAllLabel);
                spannableString.setSpan(new ForegroundColorSpan(color), start, start + allAllLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }

        }
    }

    @Nullable
    private void filterTextRouteLinkSpannableString(Context context, TextView textView, String sessionId, TextChatMessage textChatMessage, SpannableString spannableString) {
        if (textChatMessage.routeLabels == null || textChatMessage.routeLinks == null) {
            return;
        }

        int color;
        if(User.isYou(AtworkApplicationLike.baseContext, textChatMessage.from)) {
            color = context.getResources().getColor(R.color.self_link);
        } else {
            color = context.getResources().getColor(R.color.peer_link);
        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());


        List<String> routeLabelsTemp = new ArrayList<>(textChatMessage.routeLabels);
        List<String> routeLinksTemp = new ArrayList<>(textChatMessage.routeLinks);

        String text = spannableString.toString();

        HashMap<String, Integer> routeLabelIndexMap = new HashMap<>();
        for(String routeLabel: textChatMessage.routeLabels) {
            int startIndex;
            if(routeLabelIndexMap.containsKey(routeLabel)) {
                startIndex = text.indexOf(routeLabel, routeLabelIndexMap.get(routeLabel));

            } else {
                startIndex = text.indexOf(routeLabel);
            }
            int endIndex = -1;
            if(-1 != startIndex) {
                endIndex = startIndex + routeLabel.length();
                routeLabelIndexMap.put(routeLabel, endIndex);
            }


            if(-1 == startIndex) {
                continue;
            }

            int routeUrlsIndex = routeLabelsTemp.indexOf(routeLabel);
            if(0 <= routeUrlsIndex) {
                routeLabelsTemp.remove(routeUrlsIndex);
            }

            TextClickableSpan span = new TextClickableSpan(context, sessionId, textChatMessage.deliveryId, routeLabel, TEXT_LINK);

            String routeLink = CollectionsKt.getOrNull(routeLinksTemp, routeUrlsIndex);
            span.setRouteLink(routeLink);

            if(null != routeLink) {
                routeLinksTemp.remove(routeLink);
            }

            spannableString.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

//        while (textMatcher.find()) {
//
//        }
    }

    private void filterWebUrlEmailPhone(Context context, Spannable s, String sessionId, ChatPostMessage chatPostMessage) {
        try {

            int color;
            if(User.isYou(AtworkApplicationLike.baseContext, chatPostMessage.from)) {
                color = context.getResources().getColor(R.color.self_link);
            } else {
                color = context.getResources().getColor(R.color.peer_link);
            }


            if (hasAT) {

                Pattern atPattern = Pattern.compile(atPatternReg, Pattern.CASE_INSENSITIVE);
                Matcher atMatcher = atPattern.matcher(s.toString());

                while (atMatcher.find()) {
                    String text = atMatcher.group();
                    TextClickableSpan span = new TextClickableSpan(context, sessionId, chatPostMessage.deliveryId, text, AT);
                    s.setSpan(span, atMatcher.start(), atMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new ForegroundColorSpan(color), atMatcher.start(), atMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }

            Pattern phonePattern = Pattern.compile(phonePatternRegCommon, Pattern.CASE_INSENSITIVE);
            Matcher phoneMatcher = phonePattern.matcher(s.toString());

            Pattern webUrlPattern = Pattern.compile(webUrlPatternReg, Pattern.CASE_INSENSITIVE);
            String test = BaseApplicationLike.baseContext.getString(R.string.share_file_name);
            if(s.toString().contains(test)) {
                fileStr = s.toString().split("\n")[0] + "\n";
                webStr = s.toString().split("\n")[1];
            }else{
                fileStr = "";
                webStr = s.toString();
            }
            Matcher webUrlMatcher = webUrlPattern.matcher(webStr);

            Pattern workplusSchemaUrlPattern = Pattern.compile(workplusSchemaUrlPatternReg, Pattern.CASE_INSENSITIVE);
            Matcher workplusSchemaUrlMatcher = workplusSchemaUrlPattern.matcher(s.toString());

            Pattern emailPattern = Pattern.compile(emailPatternReg, Pattern.CASE_INSENSITIVE);
            Matcher emailMatcher = emailPattern.matcher(s.toString());

            while (emailMatcher.find()) {
                String emailStr = emailMatcher.group();
                TextClickableSpan span = new TextClickableSpan(context, sessionId, chatPostMessage.deliveryId,emailStr, EMAIL);
                s.setSpan(span, emailMatcher.start(), emailMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), emailMatcher.start(), emailMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new UnderlineSpan(), emailMatcher.start(), emailMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            while(workplusSchemaUrlMatcher.find()) {
                String schemaUrlText = workplusSchemaUrlMatcher.group();
                TextClickableSpan span = new TextClickableSpan(context, sessionId, chatPostMessage.deliveryId,schemaUrlText, WORKPLUS_SCHEMA_URL);
                s.setSpan(span, workplusSchemaUrlMatcher.start(), workplusSchemaUrlMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), workplusSchemaUrlMatcher.start(), workplusSchemaUrlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new UnderlineSpan(), workplusSchemaUrlMatcher.start(), workplusSchemaUrlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            while (webUrlMatcher.find()) {
                String text = webUrlMatcher.group();
                TextClickableSpan span = new TextClickableSpan(context, sessionId, chatPostMessage.deliveryId, text, WEB_URL);
                s.setSpan(span, fileStr.length()+webUrlMatcher.start(), fileStr.length()+webUrlMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), fileStr.length()+webUrlMatcher.start(), fileStr.length()+webUrlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new UnderlineSpan(), fileStr.length()+webUrlMatcher.start(), fileStr.length()+webUrlMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //检测电话号码
            while (phoneMatcher.find()) {
                String phoneText = phoneMatcher.group();
                TextClickableSpan span = new TextClickableSpan(context, sessionId, chatPostMessage.deliveryId,phoneText, PHONE);
                s.setSpan(span, phoneMatcher.start(), phoneMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new ForegroundColorSpan(color), phoneMatcher.start(), phoneMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                s.setSpan(new UnderlineSpan(), phoneMatcher.start(), phoneMatcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


        } catch (Exception e) {
            LogUtil.e("AutoLinkHelper", e.getMessage(), e);
        }
    }

    class TextClickableSpan extends ClickableSpan {
        private Context context;
        private String text;
        private String sessionId;
        private String messageId;
        private int intentFlag = 0;
        private String routeLink;

        public TextClickableSpan(Context context,  String sessionId, String messageId, String text, int intentFlag) {
            this.sessionId = sessionId;
            this.messageId = messageId;
            this.text = text;
            this.context = context;
            this.intentFlag = intentFlag;
        }

        public TextClickableSpan setRouteLink(String routeLink) {
            this.routeLink = routeLink;
            return this;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(Color.rgb(51, 102, 205));
            if (intentFlag == WEB_URL) {
                ds.setUnderlineText(true);
            } else {
                ds.setUnderlineText(false);
            }
        }


        @Override
        public void onClick(View view) {
            if (isLongClick) {
                isLongClick = false;
                return;
            }
            switch (intentFlag) {
                case WEB_URL:
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(text).setSessionId(sessionId);
                    context.startActivity(WebViewActivity.getIntent(context, webViewControlAction));
//                    context.startActivity(new Intent(context, MainActivity.class));
                    break;

                case WORKPLUS_SCHEMA_URL:
                    SchemaUrlJumpHelper.handleUrl(context, text);
                    break;

                case EMAIL:
                    ChatDetailExposeBroadcastSender.transparentViewMessage(text, PhoneEmailPopupWindow.EMAIL);
//                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//                    emailIntent.setData(Uri.parse("mailto:" + text));
//                    startChooseActivity(emailIntent, "发送邮件");
                    break;
                case PHONE:
                    ChatDetailExposeBroadcastSender.transparentViewMessage(text,PhoneEmailPopupWindow.PHONE);
                    break;
                case TEXT_LINK:
                    WebViewControlAction routeAction = WebViewControlAction.newAction().setUrl(routeLink);
                    UrlRouteHelper.routeUrl(context, routeAction);
                    break;
                default:
                    break;
            }
        }


        private void startChooseActivity(Intent sendIntent, String title) {
            Intent chooser = Intent.createChooser(sendIntent, title);

            if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(chooser);
            } else {
                AtworkToast.showToast("手机中找不到合适的应用进行此操作:" + title);
            }
        }
    }

}
