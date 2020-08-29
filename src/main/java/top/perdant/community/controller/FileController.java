package top.perdant.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import top.perdant.community.dto.FileDTO;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.exception.CustomizeException;
import top.perdant.community.provider.AliyunProvider;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    AliyunProvider aliyunProvider;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        FileDTO fileDTO = new FileDTO();
        try {
            String fileUrl = aliyunProvider.upload(file.getInputStream(), file.getOriginalFilename());
            fileDTO.setSuccess(1);
            fileDTO.setMessage("上传成功");
            fileDTO.setUrl(fileUrl);
        } catch (Exception e) {
            throw new CustomizeException(CustomizeErrorCode.UPLOAD_FILE_FAIL);
        }
        return fileDTO;
    }
}
