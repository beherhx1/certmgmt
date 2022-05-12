package com.example.demo20.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

@Service
@Validated
public class FileStorageServiceImpl implements FileStorageService {
	public final Path root = Paths.get("C:\\uploads");
	
	private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

	@Override
	public void save(MultipartFile file) throws IOException {
	    	logger.info("Upload File Location: " + root.toString());
	        Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
	}

	@Override
	public Resource load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

}
