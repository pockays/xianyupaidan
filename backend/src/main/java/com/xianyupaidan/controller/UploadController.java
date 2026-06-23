package com.xianyupaidan.controller;

import com.xianyupaidan.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class UploadController {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<List<String>> upload(@RequestParam("files") List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            try {
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);
                String name = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
                Path target = dir.resolve(name);
                file.transferTo(target.toFile());
                urls.add("/uploads/" + name);
            } catch (IOException e) {
                log.error("Upload failed", e);
                return Result.error("上传失败: " + e.getMessage());
            }
        }
        return Result.ok(urls);
    }
}
