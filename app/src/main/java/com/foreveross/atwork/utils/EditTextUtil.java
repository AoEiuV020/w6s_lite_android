package com.foreveross.atwork.utils;

import android.text.InputFilter;
import android.widget.EditText;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

/**
 * Created by dasunsy on 2017/4/26.
 */

public class EditTextUtil {

    /**
     * 设置 editText 输入的最大长度, 区分于{@link #setEditTextMaxCharLengthInput(EditText, int, boolean)}
     * 该方法是 string 的长度, 而后者是"字符的长度", 根据需求, "字符长度"指中文占2个长度, 英文数字占 1个字符
     * 备注：（该函数并不支持区分《字符长度"指中文占2个长度, 英文数字占 1个字符》这种情况。中文英文数字都一律当成一个字符。）
     * */
    public static void setEditTextMaxStringLengthInput(EditText editText, int size, boolean needToastTip) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (!StringUtils.isEmptyNoTrim(source.toString())) {
                String newText = source.subSequence(start, end).toString();
                int destLeft = dest.length() - (dend - dstart);
                //若不做过滤处理, 文本最终的长度
                int previewLength = newText.length() + destLeft;

                if (size < previewLength) {

                    if (needToastTip) {
                        AtworkToast.showResToast(R.string.edit_text_letter_max, size);
                    }

                    int newTextLeftCount = size - destLeft;

                    //判断新输入的字符还允许多少字符进来
                    if (0 >= newTextLeftCount) {
                        return StringUtils.EMPTY;

                    } else {

                        return newText.substring(start, newTextLeftCount);

                    }
                }
            }

            return null; //null to accept the original replacement
        };

        editText.setFilters(filters);
    }


    /**
     * 设置"字符的长度", 根据需求, "字符长度"指中文占2个长度, 英文数字占 1个字符
     *
     *@see #setEditTextMaxStringLengthInput(EditText, int, boolean)
     * */
    public static void setEditTextMaxCharLengthInput(EditText editText, int size, boolean needToastTip, String toastTip) {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (!StringUtils.isEmptyNoTrim(source.toString())) {
                StringBuilder newText = new StringBuilder(source.subSequence(start, end).toString());
                int destLeftCharLength = StringUtils.getWordCount(dest.toString()) - StringUtils.getWordCount(dest.subSequence(dstart, dend).toString());
                int newTextCharLength = StringUtils.getWordCount(newText.toString());
                int previewTextCharLength = destLeftCharLength + newTextCharLength;

                if (size < previewTextCharLength) {
                    if (needToastTip) {
                        if (StringUtils.isEmpty(toastTip)) {
                            AtworkToast.showResToast(R.string.edit_text_letter_max, size);
                        } else {
                            AtworkToast.showToast(toastTip);
                        }
                    }

                    int newTextLeftCount = size - destLeftCharLength;

                    //判断新输入的字符还允许多少字符进来
                    if (0 >= newTextLeftCount) {
                        return StringUtils.EMPTY;
                    }
                    StringBuilder newTextLeft = new StringBuilder();
                    for (int i = 0; i < newText.length(); i++) {

                        if (newTextLeftCount < StringUtils.getWordCount(newTextLeft + newText.substring(i, i + 1))) {
                            break;
                        } else {
                            newTextLeft.append(newText.substring(i, i + 1));
                        }
                    }
                    return newTextLeft.toString();
                }
            }
            return null; //null to accept the original replacement
        };

        editText.setFilters(filters);
    }


    /**
     * 还原"最大输入"的设定
     */
    public static void clearMaxInput(EditText editText) {
        editText.setFilters(new InputFilter[0]);
    }
}
