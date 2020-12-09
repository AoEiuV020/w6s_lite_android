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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.foreveross.atwork.api.sdk.sticker.responseJson.StickerData;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.rockerhieu.emojicon.emoji.People;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
public class EmojiconGridFragment extends Fragment implements AdapterView.OnItemClickListener {
    private OnEmojiconClickedListener mOnEmojiconClickedListener;
    private Emojicon[] mData;

    protected static EmojiconGridFragment newInstance(Emojicon[] emojicons) {
    	EmojiconGridFragment emojiGridFragment = new EmojiconGridFragment();
        Bundle args = new Bundle();
        args.putSerializable("emojicons", emojicons);
        emojiGridFragment.setArguments(args);
        return emojiGridFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.emojicon_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GridView gridView = (GridView) view.findViewById(R.id.Emoji_GridView);
        mData = getArguments() == null ? People.DATA : (Emojicon[]) getArguments().getSerializable("emojicons");
        gridView.setAdapter(new EmojiAdapter(view.getContext(), mData));
        gridView.setOnItemClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("emojicons", mData);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnEmojiconClickedListener != null) {
            mOnEmojiconClickedListener.onEmojiconClicked((Emojicon) parent.getItemAtPosition(position));
        }
    }

    public void setOnEmojiconClickedListener(OnEmojiconClickedListener listener){
    	mOnEmojiconClickedListener = listener;
    }
    
    public interface OnEmojiconClickedListener {
        void onEmojiconClicked(Emojicon emojicon);
        void onStickerClicked(String categoryId, StickerData stickerData);
    }
}
