package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(ExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
class ExceptionHandlerTest {
    @InjectMocks
    private ExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ExceptionHandler();
    }

    @Test
    void testHandleNotFoundException() {
        NotFoundException exception = new NotFoundException("Error 404: Not Found");
        ErrorResponse response = exceptionHandler.handleNotFoundException(exception);

        assertEquals("Error 404: Not Found", response.getError());
    }

    @Test
    void testHandleMethodArgumentNotValidException() {
        BindException bindException = mock(BindException.class);
        when(bindException.getAllErrors()).thenReturn(
                List.of(new ObjectError("Error", "Error 400: Bad Request")));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindException);

        ErrorResponse response = exceptionHandler.handleMethodArgumentNotValidException(exception);

        assertEquals("Error 400: Bad Request", response.getError());
    }


    @Test
    void testHandleNotValidException() {
        NotValidException exception = new NotValidException("Error 500: Internal Server Error");
        ErrorResponse response = exceptionHandler.handleNotValidException(exception);

        assertEquals("Error 500: Internal Server Error", response.getError());
    }

    @Test
    void testHandleException() {
        Exception exception = new Exception("Error 500: Internal Server Error");
        ErrorResponse response = exceptionHandler.handleException(exception);

        assertEquals("Error 500: Internal Server Error", response.getError());
    }
}
