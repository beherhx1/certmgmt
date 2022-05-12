package com.example.demo20.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo20.Certificate;
import com.example.demo20.CertificateForm;
import com.example.demo20.InvalidSourceTypeException;
import com.example.demo20.SourceType;
import com.example.demo20.repository.CertificateRepository;
import com.example.demo20.utils.CertExpiryAlertUtils;

import io.github.millij.poi.SpreadsheetReadException;
import io.github.millij.poi.ss.reader.XlsReader;
import io.github.millij.poi.ss.reader.XlsxReader;

@Service
@Validated
public class CertificateServiceImpl implements CertificateService {

	@Autowired
	CertificateRepository<Certificate> certificateRepository;

	private static final Logger logger = LoggerFactory.getLogger(CertificateServiceImpl.class);

	@Override
	@Transactional
	public Certificate save(String function, String hostName, String applicationName, LocalDate validFrom,
			LocalDate expiryDate, String alias) {
		if (validFrom.isAfter(expiryDate)) throw new IllegalArgumentException("Invalid expiry date: From data cannot be later than Expiry Date");
		Certificate cert = new Certificate(function, hostName, applicationName, validFrom, expiryDate, alias);

		certificateRepository.save(cert);

		return cert;
	}

	@Override
	public Certificate findCertificateByAliasAndFunction(String alias, String function) {
		// TODO Auto-generated method stub
		return certificateRepository.findByAliasAndFunction(alias, function);
	}

	@Override
	public Optional<Certificate> getCertificate(long id) {
		
		Optional<Certificate> cert = Optional.ofNullable(certificateRepository.getById(id));
		
		if (cert.isPresent()) {
			return cert;
		} else {
			return null;
		}
	}
	


	@Override
	public List<Certificate> getAllCertificates() {
		return certificateRepository.findAll();
	}

	@Override
	public void updateCertificateExpiryDate(String alias, String function, LocalDate startDate, LocalDate expiryDate) {
		// TODO Auto-generated method stub
		Certificate dbCert = certificateRepository.findByAliasAndFunction(alias, function);

		if (null != dbCert) {
			dbCert.setValidFrom(startDate);
			dbCert.setExpiryDate(expiryDate);
			certificateRepository.save(dbCert);
		}
	}

	/**
	 * @param SourceType
	 * @param Excel File url
	 * Reads an Excel file
	 * Writes each row as Certificate object
	 * @return List of Certificates
	 */
	@Override
	public void bulk_saveFromExternalSource(SourceType sourceType, MultipartFile multipartFile) throws InvalidSourceTypeException, IOException {
	
		List<Certificate> certs = new ArrayList<Certificate>();
		logger.info("CertificateServiceImpl::bulk_saveFromExternalSource::File name: " + multipartFile.getOriginalFilename());

		if (multipartFile.getOriginalFilename().endsWith(".xlsx")) {
//			File convFile = new File( multipartFile.getOriginalFilename() );
//			convFile.createNewFile();
			try {
		        //FileOutputStream fos = new FileOutputStream( convFile );
		        //fos.write( multipartFile.getBytes() );
		        //fos.close();
		        //Workbook workbook = CertExpiryAlertUtils.readWorkBook2(convFile); 
				//Sheet aSheet = CertExpiryAlertUtils.getSheetFromWorkBook(workbook, 0);
				certs = rowsToCerts(multipartFile, 0); // Reads First Sheet
				saveAll(certs);
			} catch (SpreadsheetReadException sre) {
				logger.error("CertificateServiceImpl::bulk_saveFromExternalSource::Unable to read Spreadsheet" + sre.getMessage());
				sre.printStackTrace();
			} catch (Exception e) {
				logger.error("CertificateServiceImpl::bulk_saveFromExternalSource::Unexpected Error occurred" + e.getMessage());
				e.printStackTrace();
			}

		} else {
			logger.error("CertificateServiceImpl::bulk_saveFromExternalSource::Unexpected Error occurred");
			
			throw new InvalidSourceTypeException("Invalid Source Type: Not an Excel File");
		}
		
	}

	private List<Certificate> rowsToCerts(MultipartFile xlsxFile, int sheetNo) throws SpreadsheetReadException, IOException {

		final XlsxReader reader = new XlsxReader();
		XSSFWorkbook workbook = new XSSFWorkbook(xlsxFile.getInputStream());
		XSSFSheet worksheet = workbook.getSheetAt(0);
		List<Certificate> certs = new ArrayList<Certificate>();
		for (int i=1; i<worksheet.getPhysicalNumberOfRows();i++) {
			Certificate cert = new Certificate();
			XSSFRow row = worksheet.getRow(i);
			
			cert.setFunction(row.getCell(0).getStringCellValue());

			if (null != row.getCell(1))  
				cert.setHostName(row.getCell(1).getStringCellValue());
			else cert.setHostName("");
			cert.setApplicationName(row.getCell(2).getStringCellValue());
			cert.setValidFrom(fromDate(row.getCell(3).getDateCellValue()));
			cert.setExpiryDate(fromDate(row.getCell(4).getDateCellValue()));
			cert.setDuration(row.getCell(5).getStringCellValue());
			cert.setDurationToExpiryDays(Math.round(row.getCell(6).getNumericCellValue()));
			if (null != row.getCell(7).getStringCellValue())  
				cert.setKeyStoreLocation(row.getCell(7).getStringCellValue());
			else cert.setKeyStoreLocation("");

			if (null != row.getCell(8))  
				cert.setAlias(row.getCell(8).getStringCellValue());
			else cert.setAlias("");
			if (null != row.getCell(9))  
				cert.setKeyStoreType(row.getCell(9).getStringCellValue());
			else cert.setKeyStoreType("");
			
			if (null != row.getCell(10))  
				cert.setIssuer(row.getCell(10).getStringCellValue());
			else cert.setIssuer("");
			
			if (null != row.getCell(11))  
				cert.setSme(row.getCell(11).getStringCellValue());
			else cert.setSme("");			
			if (null != row.getCell(12))  
				cert.setCreationTool(row.getCell(12).getStringCellValue());
			else cert.setCreationTool("");			
			certs.add(cert);
			
		}
		//File file = new File(xlsxFile.getOriginalFilename());
		//File file = new File("C:\\uploads\\RCO-Certificates_Tracking.xlsx");
		//xlsxFile.transferTo(file);
		
		//List<CertificateForm> certForms = reader.read(CertificateForm.class, file, sheetNo);
		
		logger.debug(" Certificate count after saving = " + certs.size());
		return certs;
	}

	private void saveAll(List<Certificate> certs) {
		for (Certificate cert : certs) {
			certificateRepository.save(cert);
		}
	}
	
	private List<Certificate> toCertificates(List<CertificateForm> certForms) {
		List<Certificate> certs = new ArrayList<Certificate>();
		for (CertificateForm f : certForms) {
			Certificate c = new Certificate(f);
			certs.add(c);
			if (null == f.getFunction() || f.getFunction().isEmpty()) break;
		}
		return certs;
	}
	
	private LocalDate fromDate(Date aDate) {
		return aDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
