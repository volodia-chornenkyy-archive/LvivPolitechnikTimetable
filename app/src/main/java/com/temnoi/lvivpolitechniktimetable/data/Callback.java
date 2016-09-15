package com.temnoi.lvivpolitechniktimetable.data;

/**
 * @author chornenkyy@gmail.com
 * @since 14.09.2016
 */

public interface Callback<T> {

    void onSuccess(T data);

    void onFailure(Throwable t);
}
