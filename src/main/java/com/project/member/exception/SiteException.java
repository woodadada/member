package com.project.member.exception;

import com.project.member.constant.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SiteException extends RuntimeException {
    private final ErrorCode errorCode;
}