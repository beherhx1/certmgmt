package com.example.demo20.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.logging.log4j.message.Message;
import org.aspectj.bridge.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo20.Certificate;
import com.example.demo20.service.CertificateService;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/api/certs")
public class Demo20RestCertController {
	
	@Autowired
	private CertificateService certificateService;
	
	private final TaskExecutor taskExecutor;
	
	public Demo20RestCertController(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}
	
	@GetMapping(value = "/api/certs")
	CompletableFuture<List<Certificate>> findAllCerts() {
		
		return CompletableFuture.supplyAsync(()->{
			randomDelay();
			List<Certificate> certlist = certificateService.getAllCertificates();
			return certlist;
		}, taskExecutor);
		
	}
	
	@GetMapping("/{id}")
	ResponseEntity<Certificate> findCertById(@PathVariable(value="id") int id) {
		final Optional<Certificate> cert = certificateService.getCertificate(id);
		
		if (cert.isPresent()) {
			return new ResponseEntity<>(cert.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	private void randomDelay() {
        try
        {
              Thread.sleep(ThreadLocalRandom.current().nextInt(5000));
        } 
        catch (InterruptedException e) 
        {
              Thread.currentThread().interrupt();
        }
	}

}
