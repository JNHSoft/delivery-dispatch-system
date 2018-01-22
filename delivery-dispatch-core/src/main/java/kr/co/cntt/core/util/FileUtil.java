package kr.co.cntt.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cntt.core.enums.ErrorCodeEnum;
import kr.co.cntt.core.exception.CnttBizException;
import kr.co.cntt.core.service.ServiceSupport;

/**
 * <p>kr.co.cntt.core.util
 * <p>FileUtil.java
 * <p>파일 유틸
 * @author JIN
 */
@Component
public class FileUtil extends ServiceSupport {
	/**
	 * <p>fileUpload
	 * <p>파일 단일 업로드
	 * @param uploadFile 업로드 파일
	 * @param savePath 저장 경로
	 * @return 파일 이름
	 * @author JIN
	 */
	public String fileUpload(MultipartFile uploadFile, String savePath) {
		String originalFileName = null;
		try {
			if (uploadFile != null) { 
				String directoryPath = savePath;
				// 파일 이름
				originalFileName = uploadFile.getOriginalFilename().replaceAll("^.*\\/|^.*\\\\", "");
				
				// 업로드 경로
				savePath += originalFileName;
				
				// 파일 저장
				byte[] fileData = uploadFile.getBytes();
				File checkDirectory = new File(directoryPath);
				if (!checkDirectory.isDirectory()) {
					checkDirectory.mkdirs();
				}
				FileOutputStream output = new FileOutputStream(savePath);
				output.write(fileData);
				output.close();
			}
		} catch (Exception e) {
			// 파일 업로드 실패 또는 DB 등록 실패하면 throw
			throw new CnttBizException.Builder(getMessage(ErrorCodeEnum.F0001)).responseCode(ErrorCodeEnum.F0001.name()).build();
		}
		return originalFileName;
	}
	/**
	 * <p>fileUpload
	 * <p>파일 업로드 멀티
	 * @param multipartFileArray 업로드 파일 배열
	 * @param savePath 저장 경로
	 * @return 맵<K, V> (k:파일input name, v:파일 이름)
	 * @author JIN
	 */
	public Map<String, String> fileUpload(MultipartFile[] multipartFileArray, String savePath) {
		Map<String, String> returnMap = new HashMap<String, String>();
		try {
			for (MultipartFile uploadFile : multipartFileArray) {
				String fileInputName = "";
				String originalFileName = "";
				String initSavePath = "";
				if (uploadFile != null) { 
					String directoryPath = savePath;
					fileInputName = uploadFile.getName();
					// 파일 이름
					originalFileName = uploadFile.getOriginalFilename().replaceAll("^.*\\/|^.*\\\\", "");
					
					// 업로드 경로
					initSavePath = savePath + originalFileName;
					
					// 파일 저장
					byte[] fileData = uploadFile.getBytes();
					File checkDirectory = new File(directoryPath);
					if (!checkDirectory.isDirectory()) {
						checkDirectory.mkdirs();
					}
					FileOutputStream output = new FileOutputStream(initSavePath);
					output.write(fileData);
					output.close();
					returnMap.put(fileInputName, originalFileName);
				}
			}
		} catch (Exception e) {
			// 파일 업로드 실패 또는 DB 등록 실패하면 throw
			throw new CnttBizException.Builder(getMessage(ErrorCodeEnum.F0001)).responseCode(ErrorCodeEnum.F0001.name()).build();
		}
		return returnMap;
	}
	/**
	 * <p>fileDelete
	 * <p>파일 삭제
	 * @param savePath 파일 저장 경로
	 * @author JIN
	 */
	public void fileDelete(String savePath) {
		File deleteFile = new File(savePath);
		if (deleteFile.isFile()) {
			deleteFile.delete();
		}
	}
	/**
	 * <p>fileDelete
	 * <p>멀티 파일 삭제
	 * @param savePathArray 저장 파일
	 * @param filePath 파일 경로
	 * @author JIN
	 */
	public void fileDelete(String[] savePathArray, String filePath) {
		for (String savePath : savePathArray) {
			File deleteFile = new File(filePath + savePath);
			if (deleteFile.isFile()) {
				deleteFile.delete();
			}
		}
	}
}
