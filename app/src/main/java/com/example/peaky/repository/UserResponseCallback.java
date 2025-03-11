package com.example.peaky.repository;

import com.example.peaky.model.User;

public interface UserResponseCallback<T> {
    void onComplete(T result);
}
