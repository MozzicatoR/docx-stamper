package org.wickedsource.docxstamper.api.typeresolver;

import pro.verron.officestamper.api.ObjectResolver;

/**
 * <p>
 * A type resolver is responsible for mapping an object of a certain Java class to an object of the DOCX4J api that
 * can be put into the .docx document. Type resolvers are used to replace
 * expressions within the .docx template.
 * </p>
 * <p>
 * Example: if an expression returns a Date object as result, this date object is passed to a DateResolver which
 * creates a org.docx4j.wml.R object (run of text) containing the properly formatted date string.
 * </p>
 * <p>
 * To use your own type resolver, implement this interface and register your implementation by calling
 * DocxStamper.getTypeResolverRegistry().addTypeResolver().
 * </p>
 *
 * @param <T> the type of the object this type resolver is responsible for.
 * @author Joseph Verron
 * @author Tom Hombergs
 * @version ${version}
 * @since 1.0.0
 * @deprecated as of version 1.6.7, replaced by {@link ObjectResolver}.
 * The new resolver is more versatile, requires less reflection mechanism,
 * and simplifies the internal workings of the docx-stamper project.
 */
@Deprecated(since = "1.6.7", forRemoval = true)
public interface ITypeResolver<T>
        extends pro.verron.officestamper.api.ITypeResolver<T> {
}
