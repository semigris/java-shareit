package ru.practicum.shereit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.exception.ErrorResponse;
import ru.practicum.shareit.exception.ExceptionHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(ExceptionHandler.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ShareItGateway.class)
class ExceptionHandlerTest {
    @InjectMocks
    private ExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new ExceptionHandler();
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
}
