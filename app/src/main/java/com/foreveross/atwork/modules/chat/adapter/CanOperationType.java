package com.foreveross.atwork.modules.chat.adapter;

/**
 * Created by lingen on 15/3/26.
 * Description:
 */
public enum CanOperationType {

    /**
     * 不允许加人或删除
     */
    Noting {
        @Override
        public int getCount(int count) {
            return count;
        }

        @Override
        public int getFixedPosition(int position) {
            return position;
        }
    },

    /**
     * 允许添加及删除人员
     */
    CanAddAndRemove {
        @Override
        public int getCount(int count) {
            return count + 2;
        }

        @Override
        public int getFixedPosition(int position) {
            return position - 2;
        }
    },

    /**
     * 只允许新增人员
     */
    OnlyCanAdd {
        @Override
        public int getCount(int count) {
            return count + 1;
        }

        @Override
        public int getFixedPosition(int position) {
            return position - 1;
        }
    };

    public abstract int getCount(int count);

    public abstract int getFixedPosition(int position);
}
