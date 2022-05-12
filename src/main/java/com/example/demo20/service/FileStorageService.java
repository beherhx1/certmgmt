package com.example.demo20.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	void save(MultipartFile file) throws IOException;
	Resource load(String filename);
}
