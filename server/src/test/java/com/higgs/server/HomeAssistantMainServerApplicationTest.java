package com.higgs.server;

import com.higgs.server.scv.CheckFailureException;
import com.higgs.server.scv.CheckType;
import com.higgs.server.scv.ServerVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests the main class of the Home Assistant Main Server application. The main method itself is untestable because it
 * initializes the spring context, which is not ideal for this unit test context.
 */
@ExtendWith(MockitoExtension.class)
class HomeAssistantMainServerApplicationTest {
    @Mock
    private ServerVerifier serverVerifier;

    private HomeAssistantMainServerApplication homeAssistantMainServerApplication;

    @BeforeEach
    void setUp() {
        this.homeAssistantMainServerApplication = HomeAssistantMainServerApplication.getInstance();
    }

    /**
     * Asserts that the {@link HomeAssistantMainServerApplication#main(String...)}  method calls the
     * {@link HomeAssistantMainServerApplication#init(ServerVerifier, Runnable, String[])} method, which then throws
     * an exception. I'll be honest, this is purely for test coverage.
     */
    @Test
    void testMain() {
        assertThrows(CheckFailureException.class, HomeAssistantMainServerApplication::main);
    }

    /**
     * Test for the {@link HomeAssistantMainServerApplication#init(ServerVerifier, Runnable, String[])} method. Verifies
     * method calls to instance methods on the {@link HomeAssistantMainServerApplication} class.
     */
    @Test
    void testInit() {
        final HomeAssistantMainServerApplication homeAssistantMainServerApplicationSpy = spy(this.homeAssistantMainServerApplication);
        final Runnable runnable = mock(Runnable.class);
        final ServerVerifier serverVerifier = mock(ServerVerifier.class);
        doCallRealMethod().when(homeAssistantMainServerApplicationSpy).init(any(), any());
        when(serverVerifier.check(any())).thenReturn(true);
        assertDoesNotThrow(() -> homeAssistantMainServerApplicationSpy.init(serverVerifier, runnable));
        verify(homeAssistantMainServerApplicationSpy, times(1)).loadProperties(any());
        verify(homeAssistantMainServerApplicationSpy, times(1)).check(serverVerifier, CheckType.PRE_INITIALIZE);
        verify(runnable, times(1)).run();
        verify(homeAssistantMainServerApplicationSpy, times(1)).check(serverVerifier, CheckType.POST_INITIALIZE);
    }

    /**
     * Tests that the {@link HomeAssistantMainServerApplication#check(ServerVerifier, CheckType)} method does not throw
     * an exception when the {@link ServerVerifier#check(CheckType)} method returns true.
     */
    @Test
    void testCheckPass() {
        when(this.serverVerifier.check(any(CheckType.class))).thenReturn(true);
        assertDoesNotThrow(() -> this.homeAssistantMainServerApplication.check(this.serverVerifier, CheckType.PRE_INITIALIZE));
    }

    /**
     * Tests that the {@link HomeAssistantMainServerApplication#check(ServerVerifier, CheckType)} method throws a
     * {@link RuntimeException} when the {@link ServerVerifier#check(CheckType)} method returns false.
     */
    @Test
    void testCheckFail() {
        when(this.serverVerifier.check(any(CheckType.class))).thenReturn(false);
        assertAll(
                () -> assertThat(assertThrows(CheckFailureException.class, () -> this.homeAssistantMainServerApplication.check(this.serverVerifier, CheckType.PRE_INITIALIZE))
                        .getMessage(), is(equalTo("System exited with exit code 10"))),
                () -> assertThat(assertThrows(CheckFailureException.class, () -> this.homeAssistantMainServerApplication.check(this.serverVerifier, CheckType.POST_INITIALIZE))
                        .getMessage(), is(equalTo("System exited with exit code 20")))
        );
    }

    /**
     * Tests that the {@link HomeAssistantMainServerApplication#loadProperties(List)} method loads input properties into
     * the {@link System#getProperties()} map.
     *
     * @param args The input properties to load.
     * @param expectedProps The expected properties after loading.
     */
    @ParameterizedTest
    @MethodSource("getTestLoadPropertiesParams")
    void testLoadProperties(final List<String> args, final Map<String, String> expectedProps) {
        this.homeAssistantMainServerApplication.loadProperties(args.toArray(new String[0]));
        expectedProps.forEach((key, value) -> assertAll(
                () -> assertThat(System.getProperties(), hasKey(key)),
                () -> assertThat(System.getProperties().get(key), is(equalTo(value)))
        ));
    }

    public static Stream<Arguments> getTestLoadPropertiesParams() {
        return Stream.of(
                Arguments.of(List.of("prop1=value1", "prop2=value2"), Map.of("prop1", "value1", "prop2", "value2")),
                Arguments.of(List.of("prop1 = value1", "prop2  = value2 "), Map.of("prop1", "value1", "prop2", "value2")),
                Arguments.of(List.of("--bool1", "--bool2"), Map.of("bool1", "true", "bool2", "true")),
                Arguments.of(List.of("flag1", "--bool1", "prop1=value1"), Map.of("flag1", "true", "bool1", "true", "prop1", "value1")),
                Arguments.of(List.of("--bool1=value1"), Map.of("bool1", "value1"))
        );
    }
}
