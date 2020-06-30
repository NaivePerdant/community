package top.perdant.community.advice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import top.perdant.community.exception.CustomizeException;

import javax.servlet.http.HttpServletRequest;

/**
 * 处理全局异常的API
 *
 * @author perdant
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    /**
     * 处理 Exception 类异常及其子类异常
     *
     * @param request
     * @param ex
     * @param model
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    ModelAndView handleControllerException(HttpServletRequest request, Throwable ex,
                                           Model model) {
        // 404 505 等异常 handler 不了，所以该方法无法捕获？
        if (ex instanceof CustomizeException){
            model.addAttribute("message",ex.getMessage());
        }else {
            model.addAttribute("message","不是404 505 也不是已经抛出的异常");
        }
        return new ModelAndView("error");
    }
}
