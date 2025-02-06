package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testHandleNotValidException() {
        UnavailableDataException exception = new UnavailableDataException("Error 500: Internal Server Error");
        ErrorResponse response = exceptionHandler.handleUnavailableDataException(exception);

        assertEquals("Error 500: Internal Server Error", response.getError());
    }

    @Test
    void testHandleException() {
        Exception exception = new Exception("Error 500: Internal Server Error");
        ErrorResponse response = exceptionHandler.handleException(exception);

        assertEquals("Error 500: Internal Server Error", response.getError());
    }
}
