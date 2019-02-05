package tuproject.libraryproject.controllers;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionController extends BaseController {

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView getException(RuntimeException re){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/error/error-template");

        modelAndView.addObject("message", re.getClass().isAnnotationPresent(ResponseStatus.class)
                ? re.getClass().getAnnotation(ResponseStatus.class).reason()
                : "Something went wrong");

        return modelAndView;

    }
}
