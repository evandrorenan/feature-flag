//package br.com.artemis.domain.usecases;
//
//import br.com.featureflagsdkjava.domain.model.Flag;
//import dev.openfeature.sdk.Client;
//import dev.openfeature.sdk.MutableContext;
//import org.apache.camel.CamelContext;
//import org.apache.camel.impl.DefaultCamelContext;
//import org.apache.camel.support.DefaultExchange;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//import java.util.Random;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//class FeatureTagProcessorTest {
//
//    public static final String PRIVILEGED_USER_CPF_VALUE = "77788899911";
//    public static final String USER1_CPF_CNPJ = "12345678911";
//    public static final String PRIVILEGED_USER_CPF_FIELDNAME = "privilegedUserCpf";
//    public static final String CPF_FIELDNAME = "cdCpfCnpj";
//    private final CamelContext ctx = new DefaultCamelContext();
//    private DefaultExchange exchange;
//    private MutableContext oFContext;
//    private Client mockedClient;
//
//    @BeforeEach
//    void setUp() {
//        exchange = new DefaultExchange(ctx);
//        oFContext = new MutableContext();
//        oFContext.add(CPF_FIELDNAME, USER1_CPF_CNPJ);
//        oFContext.add(PRIVILEGED_USER_CPF_FIELDNAME, PRIVILEGED_USER_CPF_VALUE);
//        exchange.getProperties().put(OpenFeatureContextBuilder.OPEN_FEATURE_CONTEXT, oFContext);
//
//        mockedClient = mock(Client.class);
//    }
//
//    @Test
//    @DisplayName("Should not break when flags list is null")
//    void shouldNotBreakWhenFlagsListIsNull() {
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, null);
//        assertDoesNotThrow(() -> usecases.process(exchange));
//    }
//
//    @Test
//    @DisplayName("Should not tag when flags are disabled")
//    void shouldNotTagWhenFlagIsDisabled() throws Exception {
//        Flag disabledFlag = disabledFlag();
//        when(this.mockedClient.getStringValue(disabledFlag.getName(), "", oFContext)).thenReturn("");
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(disabledFlag));
//
//        usecases.process(exchange);
//
//        assertNull(exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
//                "The returned tag should be null");
//    }
//
//    @Test
//    @DisplayName("Should tag when flag is enabled and have no Conditions")
//    void shouldTagWhenFlagIsEnabledAndHaveNoConditions() throws Exception {
//        Flag noConditionsFlag = noConditionsFlag();
////        when(this.mockedClient.getStringValue(noConditionsFlag.getName(), "", oFContext))
////                .thenReturn(noConditionsFlag.getOutcome());
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(noConditionsFlag));
//
//        usecases.process(exchange);
//
////        assertEquals(noConditionsFlag.getOutcome(),
////                exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
////                "The returned tag should be the same as the flag outcome");
//    }
//
//    @Test
//    @DisplayName("Should tag when flag is enabled and conditions are met")
//    void shouldTagWhenFlagIsEnabledAndConditionsAreMet() throws Exception {
//        Flag privilegedUserFlag = privilegedUserFlag();
////        when(this.mockedClient.getStringValue(privilegedUserFlag.getName(), "", oFContext))
////                .thenReturn(privilegedUserFlag.getOutcome());
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(privilegedUserFlag));
//
//        usecases.process(exchange);
//
////        assertEquals(privilegedUserFlag.getOutcome(),
////                exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
////                "The returned tag should be the same as the flag outcome");
//    }
//
//    @Test
//    @DisplayName("Should not tag when flag is enabled and conditions are not met")
//    void shouldNotTagWhenFlagIsEnabledAndConditionsAreNotMet() throws Exception {
//        Flag privilegedUserFlag = privilegedUserFlag();
//        oFContext.add(CPF_FIELDNAME, "12345678912");
//        when(this.mockedClient.getStringValue(privilegedUserFlag.getName(), "", oFContext))
//                .thenReturn("");
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(privilegedUserFlag));
//
//        usecases.process(exchange);
//
//        assertNull(exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
//                "The returned tag should be null");
//    }
//
//    @Test
//    @DisplayName("Should tag when flag is enabled and multiple conditions are met")
//    void shouldTagWhenFlagIsEnabledAndMultipleConditionsAreMet() throws Exception {
//        Flag doubleConditionFlag = doubleConditionFlag();
////        when(this.mockedClient.getStringValue(doubleConditionFlag.getName(), "", oFContext))
////                .thenReturn(doubleConditionFlag.getOutcome());
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(doubleConditionFlag));
//
//        usecases.process(exchange);
//
////        assertEquals(doubleConditionFlag.getOutcome(),
////                exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
////                "The returned tag should be the same as the flag outcome");
//    }
//
//    @Test
//    @DisplayName("Should not tag when flag is enabled and multiple conditions are not met")
//    void shouldNotTagWhenFlagIsEnabledAndMultipleConditionsAreNotMet() throws Exception {
//        Flag doubleConditionFlag = doubleConditionFlag();
//        oFContext.add(CPF_FIELDNAME, "12345678912");
//        when(this.mockedClient.getStringValue(doubleConditionFlag.getName(), "", oFContext))
//                .thenReturn("");
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, List.of(doubleConditionFlag));
//
//        usecases.process(exchange);
//
//        assertNull(exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
//                "The returned tag should be null");
//    }
//
//    @Test
//    @DisplayName("Should tag with 'conflict' when flags with no conditions are conflicting")
//    void shouldNotTagWhenFlagsWithNoConditionsAreConflicting() throws Exception {
//        List<Flag> conflictingNoConditionFlags = conflictingNoConditionFlags();
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, conflictingNoConditionFlags);
//
//        usecases.process(exchange);
//
//        assertEquals("conflict", exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
//                "The returned tag should be 'conflict'");
//    }
//
//    @Test
//    @DisplayName("Should tag with 'conflict' when flags with conditions are conflicting")
//    void shouldNotTagWhenFlagsWithConditionsAreConflicting() throws Exception {
//        List<Flag> conflictingConditionalFlags = conflictingConditionalFlags();
//
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, conflictingConditionalFlags);
//
//        usecases.process(exchange);
//
//        assertEquals("conflict", exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
//                "The returned tag should be 'conflict'");
//    }
//
//    @Test
//    @DisplayName("Should tag with conditional flag outcome when no condition flag is conflicting")
//    void shouldTagWithConditionalFlagOutcomeWhenNoConditionFlagIsConflicting() throws Exception {
//        List<Flag> noConditionAndConditionalConflictingFlags = noConditionAndConditionalConflictingFlags();
////        when(this.mockedClient.getStringValue(noConditionAndConditionalConflictingFlags.get(1).getName(), "", oFContext))
////                .thenReturn(noConditionAndConditionalConflictingFlags.get(1).getOutcome());
//        FeatureTagProcessor usecases = new FeatureTagProcessor(mockedClient, noConditionAndConditionalConflictingFlags);
//
//        usecases.process(exchange);
//
////        assertEquals(noConditionAndConditionalConflictingFlags.get(1).getOutcome(),
////                exchange.getMessage().getHeader(FeatureTagProcessor.X_FEATURE_FLAG_TAG),
////                "The returned tag should be the same as the flag outcome");
//    }
//
////    public static Flag.FlagBuilder defaultFlag(FlagConditions... flagConditions) {
////        List<FlagConditions> conditions = List.of(defaultFlagCondition().build());
////
////        if (flagConditions != null && flagConditions.length > 0) {
////            conditions = List.of(flagConditions);
////        }
//
//        return Flag.builder()
//                .name("AGRO2-" + new Random().nextInt(9999))
//                .state("ENABLED")
//                .outcome("Release1")
//                .conditions(conditions);
//    }
//
////    public static FlagConditions.FlagConditionsBuilder defaultFlagCondition() {
////        return FlagConditions.builder()
////                .fieldName(CPF_FIELDNAME)
////                .operator(Operator.EQUALS.toString())
////                .value(USER1_CPF_CNPJ);
////    }
//
//    private Flag disabledFlag() {
//        return defaultFlag().state("DISABLED").build();
//    }
//
//    private Flag noConditionsFlag(){
//        return defaultFlag().conditions(null).build();
//    }
//
//    private Flag conditionalFlag() {
//        return defaultFlag().build();
//    }
//
//    private Flag privilegedUserFlag() {
////        FlagConditions privilegedUserCondition = defaultFlagCondition().
////                fieldName(PRIVILEGED_USER_CPF_FIELDNAME).value(PRIVILEGED_USER_CPF_VALUE).build();
////        return defaultFlag(privilegedUserCondition).build();
//        return null;
//    }
//
//    private Flag doubleConditionFlag() {
////        FlagConditions privilegedUserCondition = defaultFlagCondition().
////                fieldName(PRIVILEGED_USER_CPF_FIELDNAME).value(PRIVILEGED_USER_CPF_VALUE).build();
////        return defaultFlag(privilegedUserCondition, defaultFlagCondition().build()).build();
//        return null;
//    }
//
//    private List<Flag> conflictingNoConditionFlags() {
//        Flag release1Flag = noConditionsFlag();
//        Flag release2Flag = noConditionsFlag();
////        release2Flag.setOutcome("Release2");
//
//        mockStringValue(release1Flag);
//        mockStringValue(release2Flag);
//
//        return List.of(release1Flag, release2Flag);
//    }
//
//    private List<Flag> conflictingConditionalFlags() {
//        Flag release1Flag = conditionalFlag();
//        Flag release2Flag = conditionalFlag();
////        release2Flag.setOutcome("Release2");
//
//        mockStringValue(release1Flag);
//        mockStringValue(release2Flag);
//
//        return List.of(release1Flag, release2Flag);
//    }
//
//    private List<Flag> noConditionAndConditionalConflictingFlags() {
//        Flag release1Flag = noConditionsFlag();
//        Flag release2Flag = conditionalFlag();
////        release2Flag.setOutcome("Release2");
//
//        mockStringValue(release1Flag);
//        mockStringValue(release2Flag);
//
//        return List.of(release1Flag, release2Flag);
//    }
//
//    private void mockStringValue(Flag flag) {
////        when(this.mockedClient.getStringValue(flag.getName(), "", this.oFContext))
////                .thenReturn(flag.getOutcome());
//    }
//}