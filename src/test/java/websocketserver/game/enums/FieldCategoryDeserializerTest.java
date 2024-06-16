package websocketserver.game.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FieldCategoryDeserializerTest {

    private final FieldCategoryDeserializer deserializer = new FieldCategoryDeserializer();

    private static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of("PLANT", FieldCategory.PFLANZE),
                Arguments.of("PFLANZE", FieldCategory.PFLANZE),
                Arguments.of("ENERGY", FieldCategory.ENERGIE),
                Arguments.of("ENERGIE", FieldCategory.ENERGIE),
                Arguments.of("WATER", FieldCategory.WASSER),
                Arguments.of("WASSER", FieldCategory.WASSER),
                Arguments.of("ROBOT", FieldCategory.ROBOTER),
                Arguments.of("ROBOTER", FieldCategory.ROBOTER),
                Arguments.of("SPACE_SUIT", FieldCategory.RAUMANZUG),
                Arguments.of("RAUMANZUG", FieldCategory.RAUMANZUG),
                Arguments.of("PLANNING", FieldCategory.PLANUNG),
                Arguments.of("PLANUNG", FieldCategory.PLANUNG),
                Arguments.of("WILDCARD", FieldCategory.ANYTHING),
                Arguments.of("ANYTHING", FieldCategory.ANYTHING),
                Arguments.of("UNKNOWN", null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    void testDeserialize(String input, FieldCategory expected) throws IOException {
        JsonParser parser = Mockito.mock(JsonParser.class);
        DeserializationContext ctxt = Mockito.mock(DeserializationContext.class);

        when(parser.getText()).thenReturn(input);

        FieldCategory result = deserializer.deserialize(parser, ctxt);
        assertEquals(expected, result);
    }
}