package com.global.aod.interview.techtest.model;

import lombok.Getter;

@Getter
public enum Fields {
    ID("id"), NAME("name");

    private String field;

    Fields(String field) {
        this.field = field;
    }
}
