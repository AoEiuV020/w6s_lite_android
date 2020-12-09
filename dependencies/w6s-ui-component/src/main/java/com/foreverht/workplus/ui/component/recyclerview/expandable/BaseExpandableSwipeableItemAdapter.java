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

package com.foreverht.workplus.ui.component.recyclerview.expandable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreverht.workplus.ui.component.recyclerview.swipeable.annotation.SwipeableItemDrawableTypes;
import com.foreverht.workplus.ui.component.recyclerview.swipeable.annotation.SwipeableItemReactions;


public interface BaseExpandableSwipeableItemAdapter<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder> {
    /**
     * Called when user is attempt to swipe the group item.
     *
     * @param holder The ViewHolder which is associated to item user is attempt to start swiping.
     * @param groupPosition Group position.
     * @param x Touched X position. Relative from the itemView's top-left.
     * @param y Touched Y position. Relative from the itemView's top-left.

     * @return Reaction type. Bitwise OR of these flags;
     */
    @SwipeableItemReactions
    int onGetGroupItemSwipeReactionType(@NonNull GVH holder, int groupPosition, int x, int y);

    /**
     * Called when user is attempt to swipe the child item.
     *
     * @param holder The ViewHolder which is associated to item user is attempt to start swiping.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param x Touched X position. Relative from the itemView's top-left.
     * @param y Touched Y position. Relative from the itemView's top-left.

     * @return Reaction type. Bitwise OR of these flags;
     */
    @SwipeableItemReactions
    int onGetChildItemSwipeReactionType(@NonNull CVH holder, int groupPosition, int childPosition, int x, int y);

    /**
     * Called when started swiping a group item.
     *
     * Call the {@link RecyclerView.Adapter#notifyDataSetChanged()} method in this callback to get the same behavior with v0.10.x or before.
     *
     * @param holder The ViewHolder that is associated the swiped item.
     * @param groupPosition Group position.
     */
    void onSwipeGroupItemStarted(@NonNull GVH holder, int groupPosition);

    /**
     * Called when started swiping a child item.
     *
     * Call the {@link RecyclerView.Adapter#notifyDataSetChanged()} method in this callback to get the same behavior with v0.10.x or before.
     *
     * @param holder The ViewHolder that is associated the swiped item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     */
    void onSwipeChildItemStarted(@NonNull CVH holder, int groupPosition, int childPosition);

    /**
     * Called when sets background of the swiping group item.
     *
     * @param holder The ViewHolder which is associated to the swiping item.
     * @param groupPosition Group position.
     * @param type Background type. One of the
     */
    void onSetGroupItemSwipeBackground(@NonNull GVH holder, int groupPosition, @SwipeableItemDrawableTypes int type);


    /**
     * Called when sets background of the swiping child item.
     *
     * @param holder The ViewHolder which is associated to the swiping item.
     * @param groupPosition Group position.
     * @param childPosition Child position.
     * @param type Background type. One of the
     */
    void onSetChildItemSwipeBackground(@NonNull CVH holder, int groupPosition, int childPosition, @SwipeableItemDrawableTypes int type);
}
