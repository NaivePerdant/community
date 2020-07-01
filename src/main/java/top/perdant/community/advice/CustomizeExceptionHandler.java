package top.perdant.community.advice;

import com.alibaba.fastjson.JSON;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import top.perdant.community.dto.ResultDTO;
import top.perdant.community.exception.CustomizeErrorCode;
import top.perdant.community.exception.CustomizeException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理全局异常的API
 *
 * @author perdant
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    /**
     * 统一处理处理 Exception 类异常及其子类异常
     *
     * @param request
     * @param ex
     * @param model
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    ModelAndView handleControllerException(HttpServletRequest request, Throwable ex,
                                           Model model, HttpServletResponse response) {
        String contentType = request.getContentType();
        if ("application/json".equals(contentType)) {
            // 返回json
            ResultDTO resultDTO;
            if (ex instanceof CustomizeException){
                resultDTO = ResultDTO.errorOf((CustomizeException) ex);
            }else {
                resultDTO = ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {
            }
            return null;
        }
        else {
            // 错误页面跳转
            if (ex instanceof CustomizeException){
                model.addAttribute("message",ex.getMessage());
            }else {
                model.addAttribute("message",CustomizeErrorCode.SYSTEM_ERROR);
            }
            return new ModelAndView("error");
        }
    }
}
