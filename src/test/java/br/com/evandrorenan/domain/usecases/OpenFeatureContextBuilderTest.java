//package br.com.artemis.domain.usecases;
//
//import br.com.artemis.security.CurrentUser;
//import br.com.artemis.security.UserProfileResolver;
//import br.com.featureflagsdkjava.model.Flag;
//import br.com.featureflagsdkjava.model.FlagConditions;
//import dev.openfeature.sdk.MutableContext;
//import org.apache.camel.CamelContext;
//import org.apache.camel.impl.DefaultCamelContext;
//import org.apache.camel.support.DefaultExchange;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Map;
//
//import static br.com.artemis.domain.usecases.FeatureTagProcessorTest.*;
//import static br.com.artemis.domain.usecases.OpenFeatureContextBuilder.OPEN_FEATURE_CONTEXT;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class OpenFeatureContextBuilderTest {
//
//    private final CamelContext ctx = new DefaultCamelContext();
//    private DefaultExchange exchange;
//
//    @BeforeEach
//    void setUp() {
//        exchange = new DefaultExchange(ctx);
//    }
//
//    @Test
//    @DisplayName("Should not break if flags is null and is not authenticated")
//    void shouldNotBreakIfFlagsIsNullAndIsNotAuthenticated() {
//        OpenFeatureContextBuilder openFeatureContextBuilder = new OpenFeatureContextBuilder(null, null);
//        assertDoesNotThrow(() -> openFeatureContextBuilder.process(this.exchange));
//    }
//
//    @Test
//    @DisplayName("Should set properties in exchange with fieldnames used on conditional flags")
//    void shouldSetPropertiesInExchangeWithFieldNamesUsedOnConditionalFlags() throws Exception {
//        Flag condFlag1 = buildCondFlag1();
//        Flag condFlag2 = buildCondFlag2();
//        this.exchange.getMessage().setBody(buildRequestBody());
//        UserProfileResolver mockedUPR = getMockedUserProfileResolver();
//
//        OpenFeatureContextBuilder openFeatureContextBuilder =
//                new OpenFeatureContextBuilder(List.of(condFlag1, condFlag2), mockedUPR);
//
//        openFeatureContextBuilder.process(this.exchange);
//
//        MutableContext mutableContext = this.exchange.getProperty(OPEN_FEATURE_CONTEXT, MutableContext.class);
//        Map<String, String> expectedFields = expectedFields();
//
//        assertEquals(expectedFields.size(), mutableContext.asMap().size(), "Size of the context does not match");
//        expectedFields.forEach((key, value) -> {
//            assertTrue(mutableContext.asMap().containsKey(key), "Key " + key + " is missing in the context");
//            assertEquals(value, mutableContext.asMap().get(key).asString(), "Value for key " + key + " does not match");
//        });
//
//    }
//
//    private static UserProfileResolver getMockedUserProfileResolver() {
//        UserProfileResolver mockUPR = mock(UserProfileResolver.class);
//        CurrentUser currentUser = CurrentUser.builder().cpf(PRIVILEGED_USER_CPF_VALUE).build();
//        when(mockUPR.getCurrentUser()).thenReturn(currentUser);
//        when(mockUPR.isAdvisor()).thenReturn(true);
//        when(mockUPR.isSpecialist()).thenReturn(false);
//        when(mockUPR.isBackofficeOperator()).thenReturn(false);
//        return mockUPR;
//    }
//
//    private static Flag buildCondFlag2() {
//        FlagConditions cond5 = defaultFlagCondition().fieldName("field5-boolean").build();
//        FlagConditions cond6 = defaultFlagCondition().fieldName("field6-null").build();
//        FlagConditions cond7 = defaultFlagCondition().fieldName("field7-nested").build();
//        FlagConditions cond8 = defaultFlagCondition().fieldName("field8-object-inside-array").build();
//        return defaultFlag().conditions(List.of(cond5, cond6, cond7, cond8)).build();
//    }
//
//    private static Flag buildCondFlag1() {
//        FlagConditions cond1 = defaultFlagCondition().fieldName("field1-root-level").build();
//        FlagConditions cond2 = defaultFlagCondition().fieldName("field2-inside-array").build();
//        FlagConditions cond3 = defaultFlagCondition().fieldName("field3-inside-object").build();
//        FlagConditions cond4 = defaultFlagCondition().fieldName("field4-number").build();
//        return defaultFlag().conditions(List.of(cond1, cond2, cond3, cond4)).build();
//    }
//
//    private static String buildRequestBody() {
//        return """
//                {
//                    "field1-root-level": "value1",
//                    "field2-inside-array": [ "value2" ],
//                    "field3-object": { "field3-inside-object": "value3" },
//                    "field4-number": 4,
//                    "field5-boolean": true,
//                    "field6-null": null,
//                    "field7": {
//                        "field7-b": { "field7-nested": "value7"}
//                    },
//                    "field8-array": [ { "field8-object-inside-array": "value8" } ]
//                }
//                """;
//    }
//
//    private static Map<String, String> expectedFields() {
//        return Map.of("field1-root-level", "value1",
//                "field2-inside-array", "value2",
//                "field3-inside-object", "value3",
//                "field4-number", "4",
//                "field5-boolean", "true",
//                "field7-nested", "value7",
//                "field8-object-inside-array", "value8");
//    }
//}