package com.yupi.yupicturebackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import com.yupi.yupicturebackend.model.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 更为通用的文件上传服务
 * 支持图片校验，图片完整路径，以及可以整合数据万象服务进行图片解析
 */
@Service
@Slf4j
public class FileManager {  
  
    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 封装了原有的简陋的 文件上传类
     */
    @Resource  
    private CosManager cosManager;  
  
    // ...
    /**
     * 上传图片
     *
     * @param multipartFile    文件
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        //1. 校验图片
        validPicture(multipartFile);
        // 图片上传地址，加入uuid为了防止即使图片名称相同，图片id也不同
        String uuid = RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        //2.拼接地址：最终上传文件名不同于原始文件名称， 拼接了文件上传时间，uuid 和后缀，保证了文件安全性
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        //这里最终上传路径 包含了uploadPathPrefix，是因为可由用户指定上传至哪个文件夹下，这样既可以保证灵活性，又可以保证安全性。
        // 假如多个项目共用一个bucket可以在再加一个项目名称的前缀进行归类
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);
        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            //3. 上传图片结果对象（COS包里的封装类）
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            //获取图片原始信息对象（COS包里的封装类）
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装返回结果，联动修改快捷键shift+f6
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            //计算宽高
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
            uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
            uploadPictureResult.setPicWidth(picWidth);
            uploadPictureResult.setPicHeight(picHeight);
            uploadPictureResult.setPicScale(picScale); //自己计算宽高比
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            uploadPictureResult.setPicSize(FileUtil.size(file));
            uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath); // "/"为了防止没有前缀
            return uploadPictureResult;
        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            //4.删除临时文件
            this.deleteTempFile(file);
        }
    }

    /**
     * 校验文件
     *
     * @param multipartFile multipart 文件
     */
    public void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        // 删除临时文件
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }


}
