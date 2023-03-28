package com.project.member.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName   : com.blog.blogsearch.constant
 * fileName      : DictionaryKey
 * author        : kang_jungwoo
 * date          : 2023/03/19
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/19       kang_jungwoo         최초 생성
 */
@Getter
@AllArgsConstructor
public enum DictionaryKey {
    QUERY("query"),
    SORT("sort"),
    PAGE("page"),
    SIZE("size"),
    EMAIL("email"),
    BEARER("Bearer")
    ;

    private final String key;
}
