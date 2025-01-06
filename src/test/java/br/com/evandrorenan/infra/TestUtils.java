package br.com.evandrorenan.infra;

import br.com.featureflagsdkjava.domain.model.Flag;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class TestUtils {

    public static void assertFieldsEqual(Object expected, Object actual) {

        validateNonNull(expected, actual);
        buildMethodsStream(expected)
                .filter(TestUtils::getMethods)
                .forEach(compareFieldValues(expected, actual));
    }

    private static Stream<Method> buildMethodsStream(Object actual) {
        return Stream.of(actual.getClass().getMethods());
    }

    private static Consumer<Method> compareFieldValues(Object expected, Object actual) {
        return method -> {
            try {
                Object expectedValue = method.invoke(expected);
                Object actualValue = actual.getClass().getMethod(method.getName()).invoke(actual);
                if (expectedValue == null) {
                    assertEquals(expectedValue, actualValue);
                    return;
                }
                if (expectedValue.getClass().isEnum()) {
                    assertEquals(expectedValue.toString(), actualValue.toString());
                    return;
                }
                if (isCustomObject(expectedValue)) {
                    log.info("Custom object. Expected: {}, Actual: {}", expectedValue, actualValue);
                    assertFieldsEqual(expectedValue, actualValue);
                    return;
                }

                assertEquals(expectedValue, actualValue);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static void validateNonNull(Object actual, Object expected) {
        assertNotNull(actual, "Actual object is null");
        assertNotNull(expected, "Expected object is null");
    }

    public static boolean getMethods(Method method) {
        return method.getName().startsWith("get")
            && method.getParameterCount() == 0
            && Modifier.isPublic(method.getModifiers())
            && !method.getName().equalsIgnoreCase("getClass")
            && !method.getName().equalsIgnoreCase("getDeclaringClass");
    }
    private static boolean isCustomObject(Object obj) {
        if (obj == null) return false;
        return !(obj instanceof String
                || obj instanceof Number
                || obj instanceof Boolean
                || obj instanceof List
                || obj instanceof Map);
    }
}
