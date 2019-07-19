package com.github.alex353cay.protei.test.json.view;

import com.github.alex353cay.protei.test.domain.user.User;

public class StatusChangeRequest {
    private User.Status status;

    public User.Status getStatus() {
        return status;
    }

    public void setStatus(User.Status status) {
        this.status = status;
    }
}
