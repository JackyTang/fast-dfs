package com.springboot.fastdfs.service;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class FastDfsServiceTest {

    @Autowired
    private FastDfsService fastDfsService;

    @Autowired
    private FastFileStorageClient storageClient;

    @Test
    void testUploadFileWithFile() throws FileNotFoundException {
        final String fileName = "d:\\Downloads\\cat.jpg";

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(new File(fileName));
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);
    }

    @Test
    void testUploadFileWithFileName() throws FileNotFoundException {
        final String fileName = "d:\\Downloads\\cat.jpg";

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(fileName);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);
    }

    @Test
    void testUploadFileWithExtName() throws FileNotFoundException {
        String fileExtName = "png";
        final String fileName = "d:\\Downloads\\cat.jpg";

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(fileName, fileExtName);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);
    }

    @Test
    void testUploadFileWithAll() throws FileNotFoundException {
        String fileExtName = "png";
        final String fileName = "d:\\Downloads\\cat.jpg";

        log.debug("##生成Metadata##");
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("Author", "JackyTang"));
        metaData.add(new MetaData("CreateDate", "2020-08-11"));

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(fileName, fileExtName, metaData);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);

        log.debug("##获取Metadata##");
        StorePath storePath = StorePath.parseFromUrl(path);
        Set<MetaData> fetchMetaData = storageClient.getMetadata(storePath.getGroup(), storePath.getPath());
        assertEquals(fetchMetaData, metaData);
        log.debug("元数据 result={}", fetchMetaData);
    }

    @Test
    void testUploadWithBytes() throws IOException {
        String fileExtName = "png";
        final String fileName = "d:\\Downloads\\cat.jpg";
        byte[] bytes = Files.readAllBytes(Path.of(fileName));

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(bytes, fileExtName);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);
    }

    @Test
    void testUploadWithBytesAll() throws IOException {
        String fileExtName = "png";
        final String fileName = "d:\\Downloads\\cat.jpg";
        byte[] bytes = Files.readAllBytes(Path.of(fileName));

        log.debug("##生成Metadata##");
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("Author", "JackyTang"));
        metaData.add(new MetaData("CreateDate", "2020-08-11"));

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(bytes, fileExtName, metaData);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);

        log.debug("##获取Metadata##");
        StorePath storePath = StorePath.parseFromUrl(path);
        Set<MetaData> fetchMetaData = storageClient.getMetadata(storePath.getGroup(), storePath.getPath());
        assertEquals(fetchMetaData, metaData);
        log.debug("元数据 result={}", fetchMetaData);
    }

    @Test
    void testDownload() {
        String url = "http://192.168.0.202:9999/group1/M00/00/00/wKgAyl8yNwGAECEaAADJbfIXAss152.jpg";
        byte[] bytes = fastDfsService.download(url);

        try {
            //将文件保存到d盘
            FileOutputStream fileOutputStream = new FileOutputStream("d:\\911565.png");
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDeleteFile() throws FileNotFoundException {
        final String fileName = "d:\\Downloads\\cat.jpg";

        log.debug("##上传文件..##");
        String path = fastDfsService.uploadFile(fileName);
        Assert.assertNotNull(path);
        log.debug("上传文件 result={}", path);

        log.debug("##删除文件..##");
        fastDfsService.deleteFile(path);
    }
}
