package com.yupi.yupicturebackend.manager;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.yupi.yupicturebackend.config.CosClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;


/**
 * manager包为一些通用的 上传下载功能类
 * CosManager为图片通用的上传下载功能，与项目业务无关联
 * @Component: 用于类级别，标记一个类为Spring的组件（Bean）。Spring会自动扫描并注册这些类为Bean。
 *  通常用于自定义的类
 * @Bean: 用于方法级别，通常在配置类（带有@Configuration注解的类）中定义。它告诉Spring这个方法将返回一个对象，
 *  该对象应该被注册为Spring应用上下文中的Bean。通常用于第三方库中的类或需要复杂初始化的Bean
 */
@Component
public class CosManager {  
  
    @Resource
    private CosClientConfig cosClientConfig;
  
    @Resource  
    private COSClient cosClient;
  
    // ... 一些操作 COS 的方法
    /**
     * 上传对象
     *
     * @param key  唯一键，例如文件名，测试中使用路径作为key
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传图片对象（附带图片解析信息）
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        // 可参考数据万象文档：https://cloud.tencent.com/document/product/436/55378
        PicOperations picOperations = new PicOperations();
        // 1 表示返回原图信息
        picOperations.setIsPicInfo(1);
        // 构造处理参数
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }



}
