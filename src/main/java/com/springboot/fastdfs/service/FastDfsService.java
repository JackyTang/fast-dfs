package com.springboot.fastdfs.service;

import cn.hutool.core.io.FileUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * FastDfs 文件上传服务类
 *
 * @author JackyTang
 * 8/11/2020 8:59 AM
 **/
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FastDfsService {

    private final FdfsWebServer fdfsWebServer;

    private final FastFileStorageClient storageClient;

    /**
     * 根据文件对象上传一般文件
     *
     * @param file 文件对象
     * @return 文件访问地址
     * @throws FileNotFoundException 文件未找到
     */
    public String uploadFile(File file) throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(file);
        return this.uploadFile(inputStream, file.length(), FilenameUtils.getExtension(file.getName()), null);
    }

    /**
     * 根据页面文件对象上传一般文件
     *
     * @param file 文件对象
     * @return 文件访问地址
     * @throws IOException IO异常
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileExtName = FilenameUtils.getExtension(file.getOriginalFilename());
        return this.uploadFile(file.getInputStream(), file.getSize(), fileExtName, null);
    }

    /**
     * 根据文件名上传一般文件
     *
     * @param fileName 全路径文件名称
     * @return 文件访问地址
     * @throws FileNotFoundException 文件未找到
     */
    public String uploadFile(String fileName) throws FileNotFoundException {
        return this.uploadFile(fileName, null, null);
    }

    /**
     * 根据文件名和扩展名上传一般文件
     *
     * @param fileName    全路径文件名称
     * @param fileExtName 扩展名称
     * @return 文件访问地址
     * @throws FileNotFoundException 文件未找到
     */
    public String uploadFile(String fileName, String fileExtName) throws FileNotFoundException {
        return this.uploadFile(fileName, fileExtName, null);
    }

    /**
     * 根据文件名、扩展名和元数据上传一般文件
     *
     * @param fileName    全路径文件名称
     * @param fileExtName 扩展名称
     * @param metaDataSet 元数据
     * @return 文件访问地址
     * @throws FileNotFoundException 文件未找到
     */
    public String uploadFile(String fileName, String fileExtName, Set<MetaData> metaDataSet) throws FileNotFoundException {
        File file = FileUtil.newFile(fileName);
        BufferedInputStream inputStream = FileUtil.getInputStream(file);

        if (StringUtils.isBlank(fileExtName)) {
            fileExtName = FilenameUtils.getExtension(file.getName());
        }

        return this.uploadFile(inputStream, file.length(), fileExtName, metaDataSet);
    }

    /**
     * 根据文件内容和扩展名上传文件
     *
     * @param fileContent 文件内容
     * @param fileExtName 扩展名称
     * @return 文件访问地址
     */
    public String uploadFile(byte[] fileContent, String fileExtName) {
        return this.uploadFile(fileContent, fileExtName, null);
    }

    /**
     * 根据文件内容、扩展名称和元数据上传文件
     *
     * @param fileContent 文件内容
     * @param fileExtName 扩展名称
     * @param metaDataSet 元数据
     * @return 文件访问地址
     */
    public String uploadFile(byte[] fileContent, String fileExtName, Set<MetaData> metaDataSet) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);
        return this.uploadFile(inputStream, fileContent.length, fileExtName, metaDataSet);
    }

    /**
     * 根据输入流、文件大小、扩展名称和元数据文件上传
     *
     * @param inputStream 输入流
     * @param size        文件大小
     * @param fileExtName 扩展名称
     * @param metaData    元数据
     * @return 文件访问地址
     */
    public String uploadFile(InputStream inputStream, long size, String fileExtName, Set<MetaData> metaData) {
        StorePath storePath = storageClient.uploadFile(inputStream, size, fileExtName, metaData);
        return this.getResAccessUrl(storePath);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 文件url
     * @return 下载的文件字节数组
     */
    public byte[] download(String fileUrl) {
        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        return this.storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), new DownloadByteArray());
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问地址
     */
    public void deleteFile(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return;
        }

        StorePath storePath = StorePath.parseFromUrl(fileUrl);
        this.storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
    }

    /**
     * 返回文件上传成功后的地址名称ַ
     *
     * @param storePath 存储路径
     * @return 封装图片完整URL地址
     */
    private String getResAccessUrl(StorePath storePath) {
        return this.fdfsWebServer.getWebServerUrl() + storePath.getFullPath();
    }

}
