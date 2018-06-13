package com.szyyky.spring.controller;

import com.alibaba.fastjson.JSON;
import com.github.tobato.fastdfs.conn.TrackerConnectionManager;
import com.github.tobato.fastdfs.domain.FileInfo;
import com.github.tobato.fastdfs.domain.MataData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.DefaultFastFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created by zhanghua on 2018/6/12 0012
 */
@Controller
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private DefaultFastFileStorageClient fastFileStorageClient;

    @Autowired
    private TrackerConnectionManager trackerConnectionManager;

    @RequestMapping("/config")
    @ResponseBody
    public String config() {
        List<String> trackerList = trackerConnectionManager.getTrackerList();

        return JSON.toJSONString(trackerList);
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {

        try {
            byte[] bytes = FileUtils.readFileToByteArray(new File("D:\\33.jpg"));
            byte[] slaveBytes = FileUtils.readFileToByteArray(new File("D:\\mm.jpg"));

            StorePath storePath = fastFileStorageClient.uploadFile(new ByteArrayInputStream(bytes), bytes.length, "jpg", null);
            StorePath slaveFilePath = fastFileStorageClient.uploadSlaveFile(storePath.getGroup(), storePath.getPath(), new ByteArrayInputStream(slaveBytes), slaveBytes.length, "_250x250", "jpg");
            System.out.println("storePath--->" + JSON.toJSONString(storePath));
            System.out.println("slaveFilePath--->" + JSON.toJSONString(slaveFilePath));

            StorePath imageAndCrtThumbImage = fastFileStorageClient.uploadImageAndCrtThumbImage(new ByteArrayInputStream(bytes), bytes.length, "jpg", null);
            System.out.println("imageAndCrtThumbImage--->" + JSON.toJSONString(imageAndCrtThumbImage));
            ThumbImageConfig thumbImageConfig = fastFileStorageClient.getThumbImageConfig();
            String slavePath = thumbImageConfig.getThumbImagePath(imageAndCrtThumbImage.getPath());
            // 或者由客户端再记录一下从文件的前缀
            FileInfo slaveFile = fastFileStorageClient.queryFileInfo(imageAndCrtThumbImage.getGroup(), slavePath);
            System.out.println("slaveFile--->" + slaveFile + ",slavePath:" + slavePath);
//            Set<MataData> metadata = fastFileStorageClient.getMetadata(imageAndCrtThumbImage.getGroup(), slavePath);
//            for (MataData metadatum : metadata) {
//                System.out.println("metadatum--->" + metadatum);
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return "hello world";
    }
}
