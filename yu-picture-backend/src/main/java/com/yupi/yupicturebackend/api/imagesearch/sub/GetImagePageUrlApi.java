package com.yupi.yupicturebackend.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图 页面地址 第一步
 */
@Slf4j
public class GetImagePageUrlApi {

    /**
     * 获取图片页面地址
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        Map<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 获取当前时间戳
        long uptime = System.currentTimeMillis();
        // 请求地址
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;
        String acsToken = "1741494654634_1741529324401_hIBHQTxg6f8TEbCh3bU9ONV8TA8uBICnZn5NT6MASM+Lu3gxAZuzEkuve6D2sLKPRtTPDuS9wuPRaHuWHDO1xW1Skq5YrHmzq/9VFzNvT66Y5c//A9vSCe0zLtt9dJdNcTMoDP57kiKPIIwamrqFmCiYaaW2avFmOZ+gXqZxqLD4rjP70HpFq2gx2pvEh20bZkjocuUI1bPKztR7w31J1DT2P8jXjnnoh90ulbz/UcPC9xUzBYsgddSsaZJvw+uh07xSGKlMEl9jZq3cHBWJdTUjLqdZmpGQ90mQ+PDuDy3rkaEoWEqpKYVIIy9Dt/3DNXG9C1pDiKpZAgI1is1P9PJtVGpOR3r5RaOEjk2WtyvV9R7mOM1WranLhMpjf+lsdM58sYzazBGyXcfg46alkH0h+6jnaVTko3dO0nHbT82Ye3xUyBMwnF1JbF0GAFwg";
        try {
            // 2. 发送 POST 请求到百度接口
            HttpResponse response = HttpRequest.post(url)
                    .form(formData)
                    .header("Acs-Token", acsToken)
                    .timeout(5000)
                    .execute();
            // 判断响应状态
            if (HttpStatus.HTTP_OK != response.getStatus()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            // 解析响应
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

            // 3. 处理响应结果
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // 对 URL 进行解码
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            return searchResultUrl;
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://www.codefather.cn/logo.png";
        String result = getImagePageUrl(imageUrl);
        System.out.println("搜索成功，结果 URL：" + result);
    }
}
