package pro.verron.officestamper.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pro.verron.officestamper.test.Functions.UppercaseFunction;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static pro.verron.officestamper.preset.OfficeStamperConfigurations.standard;
import static pro.verron.officestamper.test.ContextFactory.mapContextFactory;
import static pro.verron.officestamper.test.ContextFactory.objectContextFactory;
import static pro.verron.officestamper.test.TestUtils.getResource;
import static pro.verron.officestamper.test.TestUtils.makeResource;

@DisplayName("Custom functions") class CustomFunctionTests {

    static Stream<Arguments> factories() {
        return Stream.of(argumentSet("obj", objectContextFactory()), argumentSet("map", mapContextFactory()));
    }

    static Stream<Arguments> trifunctions() {
        return Stream.of(Arguments.of(objectContextFactory(), "ZH", "2024 四月"),
                Arguments.of(objectContextFactory(), "FR", "2024 avril"),
                Arguments.of(objectContextFactory(), "EN", "2024 April"),
                Arguments.of(objectContextFactory(), "JA", "2024 4月"),
                Arguments.of(objectContextFactory(), "HE", "2024 אפריל"),
                Arguments.of(objectContextFactory(), "IT", "2024 aprile"),
                Arguments.of(mapContextFactory(), "ZH", "2024 四月"),
                Arguments.of(mapContextFactory(), "FR", "2024 avril"),
                Arguments.of(mapContextFactory(), "EN", "2024 April"),
                Arguments.of(mapContextFactory(), "JA", "2024 4月"),
                Arguments.of(mapContextFactory(), "HE", "2024 אפריל"),
                Arguments.of(mapContextFactory(), "IT", "2024 aprile"));
    }

    @DisplayName("Should allow to inject full interfaces")
    @MethodSource("factories")
    @ParameterizedTest
    void interfaces(ContextFactory factory) {
        var config = standard().exposeInterfaceToExpressionLanguage(UppercaseFunction.class, Functions.upperCase());
        var template = getResource(Path.of("CustomExpressionFunction.docx"));
        var context = factory.show();
        var stamper = new TestDocxStamper<>(config);
        var expected = """
                == Custom Expression Function
                
                In this paragraph, we uppercase a variable: THE SIMPSONS.
                In this paragraph, we uppercase some multiline text: IT ALSO WORKS WITH<br/>
                MULTILINE<br/>
                STRINGS OF TEXT.
                We toggle this paragraph display with a processor using the custom function.
                We check custom functions runs in placeholders after processing: HOMER SIMPSON.
                We check custom functions runs in placeholders after processing: MARGE SIMPSON.
                We check custom functions runs in placeholders after processing: BART SIMPSON.
                We check custom functions runs in placeholders after processing: LISA SIMPSON.
                We check custom functions runs in placeholders after processing: MAGGIE SIMPSON.
                |===
                |We check custom functions runs in placeholders after processing:
                
                |HOMER SIMPSON
                |DAN CASTELLANETA<cnfStyle=000000100000>
                
                |MARGE SIMPSON
                |JULIE KAVNER<cnfStyle=000000100000>
                
                |BART SIMPSON
                |NANCY CARTWRIGHT<cnfStyle=000000100000>
                
                |LISA SIMPSON
                |YEARDLEY SMITH<cnfStyle=000000100000>
                
                |MAGGIE SIMPSON
                |JULIE KAVNER<cnfStyle=000000100000>
                
                
                |===
                
                """;
        var actual = stamper.stampAndLoadAndExtract(template, context);
        assertEquals(expected, actual);
    }

    @DisplayName("Should allow to inject lambda functions")
    @MethodSource("factories")
    @ParameterizedTest
    void functions(ContextFactory factory) {
        var config = standard();
        config.addCustomFunction("toUppercase", String.class)
              .withImplementation(String::toUpperCase);
        var template = makeResource("${toUppercase(name)}");
        var context = factory.show();
        var stamper = new TestDocxStamper<>(config);
        var expected = """
                THE SIMPSONS
                """;
        var actual = stamper.stampAndLoadAndExtract(template, context);
        assertEquals(expected, actual);
    }

    @DisplayName("Should allow to inject lambda suppliers.")
    @MethodSource("factories")
    @ParameterizedTest
    void suppliers(ContextFactory factory) {
        var config = standard();
        config.addCustomFunction("foo", () -> List.of("a", "b", "c"));
        var template = makeResource("${foo()}");
        var context = factory.empty();
        var stamper = new TestDocxStamper<>(config);
        var expected = """
                [a, b, c]
                """;
        var actual = stamper.stampAndLoadAndExtract(template, context);
        assertEquals(expected, actual);
    }

    @DisplayName("Should allow to inject lambda suppliers.")
    @MethodSource("factories")
    @ParameterizedTest
    void bifunctions(ContextFactory factory) {
        var config = standard();
        config.addCustomFunction("Add", String.class, Integer.class)
              .withImplementation((s, i) -> new BigDecimal(s).add(new BigDecimal(i)));
        var template = makeResource("${Add('3.22', 4)}");
        var context = factory.empty();
        var stamper = new TestDocxStamper<>(config);
        var expected = """
                7.22
                """;
        var actual = stamper.stampAndLoadAndExtract(template, context);
        assertEquals(expected, actual);
    }

    @DisplayName("Should allow to inject lambda trifunctions.")
    @MethodSource
    @ParameterizedTest
    void trifunctions(ContextFactory factory, String tag, String expected) {
        var config = standard();
        config.addCustomFunction("format", LocalDate.class, String.class, String.class)
              .withImplementation((date, pattern, languageTag) -> {
                  var locale = Locale.forLanguageTag(languageTag);
                  var formatter = DateTimeFormatter.ofPattern(pattern, locale);
                  return formatter.format(date);
              });
        var template = makeResource("${format(date,'yyyy MMMM','%s')}".formatted(tag));
        var context = factory.date(LocalDate.of(2024, Month.APRIL, 1));
        var stamper = new TestDocxStamper<>(config);
        var actual = stamper.stampAndLoadAndExtract(template, context);
        assertEquals(expected + "\n", actual);
    }
}
