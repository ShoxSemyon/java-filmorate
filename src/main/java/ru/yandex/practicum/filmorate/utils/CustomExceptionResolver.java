package ru.yandex.practicum.filmorate.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import ru.yandex.practicum.filmorate.exception.IllegalFilmException;
import ru.yandex.practicum.filmorate.exception.IllegalUserException;

@Component
public class CustomExceptionResolver extends AbstractHandlerExceptionResolver {

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        if (ex instanceof IllegalFilmException || ex instanceof IllegalUserException) {
            modelAndView.setStatus(HttpStatus.BAD_REQUEST);
            modelAndView.addObject("message", ex.getMessage());
            return modelAndView;

        }
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        modelAndView.addObject("message", "Another exception was handled");
        return modelAndView;
    }


}