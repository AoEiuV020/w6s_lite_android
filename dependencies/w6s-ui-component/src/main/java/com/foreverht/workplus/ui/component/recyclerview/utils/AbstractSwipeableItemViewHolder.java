/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.foreverht.workplus.ui.component.recyclerview.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.ui.component.recyclerview.swipeable.RecyclerViewSwipeManager;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.SwipeableItemState;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.SwipeableItemViewHolder;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.annotation.SwipeableItemAfterReactions;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.annotation.SwipeableItemResults;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.annotation.SwipeableItemStateFlags;

public abstract class AbstractSwipeableItemViewHolder extends RecyclerView.ViewHolder implements SwipeableItemViewHolder {
    private SwipeableItemState mSwipeState = new SwipeableItemState();
    @SwipeableItemResults
    private int mSwipeResult = RecyclerViewSwipeManager.RESULT_NONE;
    @SwipeableItemAfterReactions
    private int mAfterSwipeReaction = RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
    private boolean mIsProportionalAmountModeEnabled = true;
    private float mHorizontalSwipeAmount;
    private float mVerticalSwipeAmount;
    private float mMaxLeftSwipeAmount = RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_LEFT;
    private float mMaxUpSwipeAmount = RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_TOP;
    private float mMaxRightSwipeAmount = RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_RIGHT;
    private float mMaxDownSwipeAmount = RecyclerViewSwipeManager.OUTSIDE_OF_THE_WINDOW_BOTTOM;

    public AbstractSwipeableItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void setSwipeStateFlags(@SwipeableItemStateFlags int flags) {
        mSwipeState.setFlags(flags);
    }

    @Override
    @SwipeableItemStateFlags
    public int getSwipeStateFlags() {
        return mSwipeState.getFlags();
    }

    @Override
    @NonNull
    public SwipeableItemState getSwipeState() {
        return mSwipeState;
    }

    @Override
    public void setSwipeResult(@SwipeableItemResults int result) {
        mSwipeResult = result;
    }

    @Override
    @SwipeableItemResults
    public int getSwipeResult() {
        return mSwipeResult;
    }

    @Override
    @SwipeableItemAfterReactions
    public int getAfterSwipeReaction() {
        return mAfterSwipeReaction;
    }

    @Override
    public void setAfterSwipeReaction(@SwipeableItemAfterReactions int reaction) {
        mAfterSwipeReaction = reaction;
    }

    @Override
    public void setProportionalSwipeAmountModeEnabled(boolean enabled) {
        mIsProportionalAmountModeEnabled = enabled;
    }

    @Override
    public boolean isProportionalSwipeAmountModeEnabled() {
        return mIsProportionalAmountModeEnabled;
    }

    @Override
    public void setSwipeItemVerticalSlideAmount(float amount) {
        mVerticalSwipeAmount = amount;
    }

    @Override
    public float getSwipeItemVerticalSlideAmount() {
        return mVerticalSwipeAmount;
    }

    @Override
    public void setSwipeItemHorizontalSlideAmount(float amount) {
        mHorizontalSwipeAmount = amount;
    }

    @Override
    public float getSwipeItemHorizontalSlideAmount() {
        return mHorizontalSwipeAmount;
    }

    @Override
    @NonNull
    public abstract View getSwipeableContainerView();

    @Override
    public void setMaxLeftSwipeAmount(float amount) {
        mMaxLeftSwipeAmount = amount;
    }

    @Override
    public float getMaxLeftSwipeAmount() {
        return mMaxLeftSwipeAmount;
    }

    @Override
    public void setMaxUpSwipeAmount(float amount) {
        mMaxUpSwipeAmount = amount;
    }

    @Override
    public float getMaxUpSwipeAmount() {
        return mMaxUpSwipeAmount;
    }

    @Override
    public void setMaxRightSwipeAmount(float amount) {
        mMaxRightSwipeAmount = amount;
    }

    @Override
    public float getMaxRightSwipeAmount() {
        return mMaxRightSwipeAmount;
    }

    @Override
    public void setMaxDownSwipeAmount(float amount) {
        mMaxDownSwipeAmount = amount;
    }

    @Override
    public float getMaxDownSwipeAmount() {
        return mMaxDownSwipeAmount;
    }

    @Override
    public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping) {
    }
}
