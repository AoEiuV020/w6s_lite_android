/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rockerhieu.emojicon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData;
import com.rockerhieu.emojicon.EmojiconGridFragment.OnEmojiconClickedListener;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.w6s.emoji.EmojiLayout;
import com.w6s.emoji.IEmojiSelectedListener;

import org.jetbrains.annotations.NotNull;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconsFragment extends Fragment {

    public static final String SHOW_STICKER = "SHOW_STICKER";
//    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private OnEmojiconClickedListener mOnEmojiconClickedListener;
//    private int mEmojiTabLastSelectedIndex = -1;
//    private View[] mEmojiTabs;
    private EmojiLayout mEmojiLayout;
    private boolean mShowSticker = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emojicons, container, false);
        if (getArguments() != null) {
            mShowSticker = getArguments().getBoolean(SHOW_STICKER, true);
        }

        mEmojiLayout = view.findViewById(R.id.emoji_layout);
        mEmojiLayout.setShowStick(mShowSticker);
        mEmojiLayout.setEmotionSelectedListener(new IEmojiSelectedListener() {
            @Override
            public void onEmojiSelected(@NotNull Emojicon key) {
                mOnEmojiconClickedListener.onEmojiconClicked(key);
            }

            @Override
            public void onStickerSelected(String categoryId, @NotNull StickerData stickerData) {
                mOnEmojiconClickedListener.onStickerClicked(categoryId, stickerData);
            }
        });
//        final ViewPager emojisPager = (ViewPager) view.findViewById(R.id.emojis_pager);
//        emojisPager.setOnPageChangeListener(this);
//
//
//        EmojiconGridFragment peopleFragment =  EmojiconGridFragment.newInstance(People.DATA);
//        peopleFragment.setOnEmojiconClickedListener(mOnEmojiconClickedListener);
//
//        EmojiconGridFragment natureFragment = EmojiconGridFragment.newInstance(Nature.DATA);
//        natureFragment.setOnEmojiconClickedListener(mOnEmojiconClickedListener);
//
//        EmojiconGridFragment objectFragment = EmojiconGridFragment.newInstance(Objects.DATA);
//        objectFragment.setOnEmojiconClickedListener(mOnEmojiconClickedListener);
//
//        EmojisPagerAdapter emojisAdapter = new EmojisPagerAdapter(getFragmentManager(), Arrays.asList(
//        		peopleFragment,
//        		natureFragment,
//        		objectFragment
//        ));
//        emojisPager.setAdapter(emojisAdapter);
//
//        mEmojiTabs = new View[3];
//        mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_people);
//        mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_nature);
//        mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_objects);
//        for (int i = 0; i < mEmojiTabs.length; i++) {
//            final int position = i;
//            mEmojiTabs[i].setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    emojisPager.setCurrentItem(position);
//                }
//            });
//        }
//        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(1000, 50, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnEmojiconBackspaceClickedListener != null) {
//                    mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
//                }
//            }
//        }));
//        onPageSelected(0);
        return view;
    }

    public void setBurnMode(boolean isBurn) {
        mEmojiLayout.setBurnMode(isBurn);
    }

    public static void input(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojicon.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
        }
    }
//
//    public static void backspace(EditText editText) {
//        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
//        editText.dispatchKeyEvent(event);
//    }
//
//    @Override
//    public void onPageScrolled(int i, float v, int i2) {
//    }
//
//    @Override
//    public void onPageSelected(int i) {
//        if (mEmojiTabLastSelectedIndex == i) {
//            return;
//        }
//        switch (i) {
//            case 0:
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.length) {
//                    mEmojiTabs[mEmojiTabLastSelectedIndex].setSelected(false);
//                }
//                mEmojiTabs[i].setSelected(true);
//                mEmojiTabLastSelectedIndex = i;
//                break;
//        }
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int i) {
//    }
//
//    private static class EmojisPagerAdapter extends FragmentStatePagerAdapter {
//        private List<EmojiconGridFragment> fragments;
//
//        public EmojisPagerAdapter(FragmentManager fm, List<EmojiconGridFragment> fragments) {
//            super(fm);
//            this.fragments = fragments;
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            return fragments.get(i);
//        }
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//    }
//
//    /**
//     * A class, that can be used as a TouchListener on any view (e.g. a Button).
//     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
//     * click is fired immediately, next before initialInterval, and subsequent before
//     * normalInterval.
//     * <p/>
//     * <p>Interval is scheduled before the onClick completes, so it has to run fast.
//     * If it runs slow, it does not generate skipped onClicks.
//     */
//    public static class RepeatListener implements View.OnTouchListener {
//
//        private Handler handler = new Handler();
//
//        private int initialInterval;
//        private final int normalInterval;
//        private final View.OnClickListener clickListener;
//
//        private Runnable handlerRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if (downView == null) {
//                    return;
//                }
//                handler.removeCallbacksAndMessages(downView);
//                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
//                clickListener.onClick(downView);
//            }
//        };
//
//        private View downView;
//
//        /**
//         * @param initialInterval The interval before first click event
//         * @param normalInterval  The interval before second and subsequent click
//         *                        events
//         * @param clickListener   The OnClickListener, that will be called
//         *                        periodically
//         */
//        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
//            if (clickListener == null)
//                throw new IllegalArgumentException("null runnable");
//            if (initialInterval < 0 || normalInterval < 0)
//                throw new IllegalArgumentException("negative interval");
//
//            this.initialInterval = initialInterval;
//            this.normalInterval = normalInterval;
//            this.clickListener = clickListener;
//        }
//
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            switch (motionEvent.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    downView = view;
//                    handler.removeCallbacks(handlerRunnable);
//                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
//                    clickListener.onClick(view);
//                    return true;
//                case MotionEvent.ACTION_UP:
//                case MotionEvent.ACTION_CANCEL:
//                case MotionEvent.ACTION_OUTSIDE:
//                    handler.removeCallbacksAndMessages(downView);
//                    downView = null;
//                    return true;
//            }
//            return false;
//        }
//    }
//
    public void setOnEmojiconClickedListener(OnEmojiconClickedListener listener){
    	mOnEmojiconClickedListener = listener;
    }
//
//    public void setOnEmojiconBackspaceClickedListener(OnEmojiconBackspaceClickedListener backSpaceListener){
//    	 mOnEmojiconBackspaceClickedListener = backSpaceListener;
//    }
//
//
//    public interface OnEmojiconBackspaceClickedListener {
//        void onEmojiconBackspaceClicked(View v);
//    }
//

}
