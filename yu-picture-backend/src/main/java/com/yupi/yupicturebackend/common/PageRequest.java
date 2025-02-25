package com.yupi.yupicturebackend.common;

import lombok.Data;

//通用分页请求包装类 可用于分表查询 如传来 页号 页面大小 字段 顺序
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int current = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
