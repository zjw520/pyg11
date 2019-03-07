package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UploadController {

    @Value("${fileServerUrl}")
    private String fileServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(MultipartFile file) {


        Map<String, Object> data = new HashMap<>();
        data.put("status", 500);
        try {
            String originalFilename = file.getOriginalFilename();
            byte[] bytes = file.getBytes();

//            FastDfs客户端代码
            String path = this.getClass().getResource("/fastdfs-client.conf").getPath();
            ClientGlobal.init(path);
            StorageClient storageClient = new StorageClient();
            String[] strings = storageClient.upload_file(bytes, FilenameUtils.getExtension(originalFilename), null);
            StringBuilder sb = new StringBuilder(fileServerUrl);
            sb.append("/" + strings[0]);
            sb.append("/" + strings[1]);
            String url = sb.toString();
            data.put("status", 200);
            data.put("url", sb.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
