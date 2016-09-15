package com.temnoi.lvivpolitechniktimetable.ui;

import android.view.View;

/**
 * @author chornenkyy@gmail.com
 * @since 9/15/16
 */

public interface OnItemClickListener<T> {
    void onClick(View view, T data);
}
