package com.yupi.yupicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 请求封装类 dto
 */
@Data
public class PictureUploadRequest implements Serializable {
  
    /**  就相当于保存了上传图片的草稿，上传图片并不立马创建，所以更换图片 也仅仅是更换url 而不新增id
     * 因此支持图片重复上传
     * 图片 id（用于修改）  
     */  
    private Long id;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 上传图片时 允许指定 图片名称
     */
    private String picName;

    /**
     * 空间 id
     */
    private Long spaceId;



    private static final long serialVersionUID = 1L;  
}
