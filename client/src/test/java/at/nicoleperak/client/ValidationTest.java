package at.nicoleperak.client;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void shouldNotThrowGivenValidInputLength(){
        assertDoesNotThrow( () -> Validation.assertUserInputLengthIsValid("1234", "test", 2, 4));
    }

    @Test
    void shouldThrowGivenEmptyInput(){
        assertThrows(ClientException.class, () -> Validation.assertUserInputLengthIsValid("", "test", 2, 4));
    }

    @Test
    void shouldThrowGivenShorterInput(){
        assertThrows(ClientException.class, () -> Validation.assertUserInputLengthIsValid("1", "test", 2, 4));
    }

    @Test
    void shouldThrowGivenLongerInput(){
        assertThrows(ClientException.class, () -> Validation.assertUserInputLengthIsValid("12345", "test", 1, 4));
    }

    @Test
    void shouldNotThrowGivenValidEmail(){
        assertDoesNotThrow( () -> Validation.assertEmailIsValid("test123_te-st.test@gmail.com"));
    }
    @ParameterizedTest
    @ValueSource(strings = {
            "test",
            ".com",
            "test.com",
            "@",
            "@.com",
            "test@",
            "@test.com",
            "?@test.com",
            "test@?.com",
            "test@test.t"
    })
    void shouldThrowGivenInvalidEmail(String input){
        assertThrows(ClientException.class, () -> Validation.assertEmailIsValid(input));
    }
    @Test
    void shouldReturnFalseIfPasswordsMatch() {
        assertFalse(Validation.passwordsDiffer("test123", "test123"));
    }
    @Test
    void shouldReturnTrueIfPasswordsDiffer() {
        assertTrue(Validation.passwordsDiffer("test123", "test124"));
    }

    @Test
    void shouldThrowGivenDifferingPasswords() {
        assertThrows(ClientException.class, () -> Validation.assertPasswordsMatch("test123", "test124"));
    }

    @Test
    void shouldNotThrowGivenMatchingPasswords() {
       assertDoesNotThrow( () -> Validation.assertPasswordsMatch("test123", "test123"));
    }

    @Test
    void shouldNotThrowGivenDateOfToday() {
        assertDoesNotThrow(() -> Validation.assertDateIsInPast(LocalDate.now()));
    }

    @Test
    void shouldNotThrowGivenPastDate() {
        assertDoesNotThrow(() -> Validation.assertDateIsInPast(LocalDate.now().minus(1, ChronoUnit.DAYS)));
    }

    @Test
    void shouldThrowGivenFutureDate() {
        assertThrows(ClientException.class, () -> Validation.assertDateIsInPast(LocalDate.now().plus(1, ChronoUnit.DAYS)));
    }

    @Test
    void shouldNotThrowWhenGivenStringInBigDecimalFormat() {
        assertDoesNotThrow(() -> Validation.assertAmountIsBigDecimal("1.11"));
    }

    @Test
    void shouldThrowWhenGivenStringInWrongFormat() {
        assertThrows(ClientException.class, () -> Validation.assertAmountIsBigDecimal("%123%%"));
    }
}