package com.bidding.controller;

import com.bidding.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名
            String newFilename = UUID.randomUUID().toString() + extension;

            // 创建上传目录 (按日期分文件夹)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            String datePath = sdf.format(new Date());
            File uploadDir = new File(UPLOAD_DIR + File.separator + datePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            File dest = new File(uploadDir.getAbsolutePath() + File.separator + newFilename);
            file.transferTo(dest);

            // 返回文件访问URL
            String fileUrl = "/files/" + datePath + "/" + newFilename;

            Map<String, String> map = new HashMap<>();
            map.put("name", originalFilename);
            map.put("url", fileUrl);

            return Result.success(map);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("url") String fileUrl) {
        try {
            // 从URL中提取相对路径 /files/2026/01/23/... -> 2026/01/23/...
            String relativePath = fileUrl;
            if (fileUrl.startsWith("/files/")) {
                relativePath = fileUrl.substring("/files/".length());
            }

            // 构建文件路径
            Path filePath = Paths.get(UPLOAD_DIR).resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // 获取文件名
                String filename = resource.getFilename();

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/view")
    public ResponseEntity<Resource> viewFile(@RequestParam("url") String fileUrl) {
        try {
            // 从URL中提取相对路径
            String relativePath = fileUrl;
            if (fileUrl.startsWith("/files/")) {
                relativePath = fileUrl.substring("/files/".length());
            }

            Path filePath = Paths.get(UPLOAD_DIR).resolve(relativePath).normalize();
            log.info("Viewing file: URL={}, Relative={}, Absolute={}", fileUrl, relativePath,
                    filePath.toAbsolutePath());

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // 尝试猜测内容类型
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException e) {
                    // ignore
                }
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
