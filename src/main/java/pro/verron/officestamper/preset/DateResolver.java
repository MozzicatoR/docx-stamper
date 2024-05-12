package pro.verron.officestamper.preset;

import pro.verron.officestamper.api.ObjectResolver;
import pro.verron.officestamper.api.StringResolver;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * This {@link ObjectResolver} creates a formatted date {@link String} for
 * expressions that return a {@link Date} object.
 *
 * @author Joseph Verron
 * @version ${version}
 * @since 1.6.7
 */
public final class DateResolver
        extends StringResolver<Date> {

    private final DateTimeFormatter formatter;

    /**
     * Creates a new DateResolver that uses the format "dd.MM.yyyy".
     */
    public DateResolver() {
        this(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    /**
     * Creates a new DateResolver.
     *
     * @param formatter the format to use for date formatting. See
     * {@link SimpleDateFormat}.
     */
    public DateResolver(DateTimeFormatter formatter) {
        super(Date.class);
        this.formatter = formatter;
    }

    /**
     * Resolves a formatted date string for the given {@link Date} object.
     *
     * @param date the {@link Date} object to be resolved.
     * @return the formatted date string.
     */
    @Override
    protected String resolve(Date date) {
        var zone = ZoneId.systemDefault();
        var localDate = date.toInstant()
                .atZone(zone)
                .toLocalDate();
        return formatter.format(localDate);
    }
}
