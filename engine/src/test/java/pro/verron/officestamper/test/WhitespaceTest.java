package pro.verron.officestamper.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pro.verron.officestamper.preset.OfficeStamperConfigurations;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;
import static pro.verron.officestamper.test.ContextFactory.mapContextFactory;
import static pro.verron.officestamper.test.ContextFactory.objectContextFactory;
import static pro.verron.officestamper.test.TestUtils.makeResource;

@DisplayName("Whitespaces manipulations") class WhitespaceTest {

    static Stream<Arguments> should_preserve_spaces() {
        return Stream.of(argumentSet("obj:1 space", objectContextFactory(), "Homer Simpson", "Homer Simpson"),
                argumentSet("obj:2 space", objectContextFactory(), "Homer  Simpson", "Homer  Simpson"),
                argumentSet("obj:3 space", objectContextFactory(), "Homer   Simpson", "Homer   Simpson"),
                argumentSet("map:1 space", mapContextFactory(), "Homer Simpson", "Homer Simpson"),
                argumentSet("map:2 space", mapContextFactory(), "Homer  Simpson", "Homer  Simpson"),
                argumentSet("map:3 space", mapContextFactory(), "Homer   Simpson", "Homer   Simpson"));
    }

    static Stream<Arguments> should_preserve_tabulations() {
        return Stream.of(argumentSet("obj", objectContextFactory()), argumentSet("map", mapContextFactory()));
    }

    @DisplayName("Should keep any number of spaces")
    @MethodSource
    @ParameterizedTest
    void should_preserve_spaces(ContextFactory factory, String in, String out) {
        var config = OfficeStamperConfigurations.standard();
        var template = makeResource("Space ${name}");
        var context = factory.name(in);

        var stamper = new TestDocxStamper<>(config);
        var actual = stamper.stampAndLoadAndExtract(template, context);
        var expected = "Space %s\n".formatted(out);
        assertEquals(expected, actual);
    }

    @DisplayName("Should keep tabulations as tabulations")
    @MethodSource
    @ParameterizedTest
    void should_preserve_tabulations(ContextFactory factory) {
        var config = OfficeStamperConfigurations.standard();
        var template = makeResource("Tab|TAB|${name}");
        var context = factory.name("Homer\tSimpson");

        var stamper = new TestDocxStamper<>(config);
        var actual = stamper.stampAndLoadAndExtract(template, context);
        var expected = "Tab\tHomer\tSimpson\n";
        assertEquals(expected, actual);
    }
}
