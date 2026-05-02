package kr.sinnau.platform.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Order(2) // @RestController가 아닌 일반 @Controller 에러를 잡습니다.
@ControllerAdvice(annotations = Controller.class)
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleViewError(Exception e) {
        log.error("[View Error] : ", e);
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorMessage", e.getMessage());
        mav.setViewName("error/default"); // src/main/resources/templates/error/default.html 필요
        return mav;
    }
}