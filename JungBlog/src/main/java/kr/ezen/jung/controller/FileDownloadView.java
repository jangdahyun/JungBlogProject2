package kr.ezen.jung.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.view.AbstractView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileDownloadView extends AbstractView {
	
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// 원본파일이름과 저장할 파일의 이름을 받는다.
		String ofile = (String) model.get("of");
		String sfile = (String) model.get("sf");

		File file = new File(request.getServletContext().getRealPath("upload"), sfile);
		log.debug("---------------------------------------------------------------------------------");
		log.debug("원본이름 : {}", ofile);
		log.debug("저장이름 : {}", sfile);
		log.debug("저장경로 : {}", request.getServletContext().getRealPath("upload"));
		log.debug("파일객체 : {}", file);
		log.debug("---------------------------------------------------------------------------------");
		// 다운로드 처리
		response.setContentType(getContentType());
		response.setContentLength((int) file.length());
		// 다운로드할 이름에 공백이나 한글처리를 담당
		String filename = URLEncoder.encode(ofile, "UTF-8").replaceAll("\\+", "%20");

		// 다운로드할 이름과 타입을 지정한다.
		response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\";");
		response.setHeader("Content-Transfer-Encoding", "binary");

		// 실제 다운로드를 실행한다.
		OutputStream os = response.getOutputStream(); // 다운로드를 위해 스트림을 얻는다.
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file); // 파일을 읽어서
			FileCopyUtils.copy(fis, os); // 원본이름으로 저장하기
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
		os.flush();
	}

}
