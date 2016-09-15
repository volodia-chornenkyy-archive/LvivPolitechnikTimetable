package com.temnoi.lvivpolitechniktimetable.ui.setup;

import android.view.View;

/**
 * @author chornenkyy@gmail.com
 * @since 9/15/16
 */

interface OnItemClickListener<T> {
    void onClick(View view, T data);
}
