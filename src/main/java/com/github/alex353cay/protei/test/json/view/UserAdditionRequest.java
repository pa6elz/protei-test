package com.github.alex353cay.protei.test.json.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAdditionRequest {
    private String name;
    private String email;
    private String phoneNumber;
}
