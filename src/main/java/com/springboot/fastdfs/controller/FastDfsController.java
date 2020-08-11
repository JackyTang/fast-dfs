package com.springboot.fastdfs.controller;

import com.springboot.fastdfs.service.FastDfsService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JackyTang
 * 8/11/2020 9:01 AM
 **/
@RestController
@RequestMapping("/fdfs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FastDfsController {

    private final FastDfsService fastDfsService;

    @RequestMapping("/upload")
    public Map<String, Object> upload(MultipartFile file) throws Exception {

        String url = fastDfsService.uploadFile(file);

        Map<String, Object> result = new HashMap<>(16);
        result.put("code", 200);
        result.put("msg", "上传成功");
        result.put("url", url);

        return result;
    }

    @RequestMapping("/download")
    public void download(String fileUrl, HttpServletResponse response) throws Exception {

        byte[] data = fastDfsService.download(fileUrl);

        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("test.jpg", StandardCharsets.UTF_8));

        // 写出
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(data, outputStream);
    }

}
