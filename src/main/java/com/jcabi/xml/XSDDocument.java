/*
 * Copyright (c) 2012-2025, jcabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import javax.xml.transform.Source;
import lombok.EqualsAndHashCode;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Implementation of {@link XSD}.
 *
 * <p>Objects of this class are immutable and thread-safe.
 *
 * @since 0.5
 * @deprecated This class is deprecated since 0.31.0. Instead, you can
 *  use {@link StrictXML} with a schema provided in the constructor. Otherwise,
 *  you can use {@link XMLDocument} and validate the XML against the schema
 *  via the {@link XMLDocument#validate(XML)} method.
 * @checkstyle AbbreviationAsWordInNameCheck (5 lines)
 */
@Deprecated
@EqualsAndHashCode(of = "xsd")
public final class XSDDocument implements XSD {

    /**
     * XSD document.
     */
    private final transient String xsd;

    /**
     * Public ctor, from XSD as a source.
     * @param src XSD document body
     */
    public XSDDocument(final XML src) {
        this(src.toString());
    }

    /**
     * Public ctor, from XSD as a string.
     * @param src XSD document body
     */
    public XSDDocument(final String src) {
        this.xsd = src;
    }

    /**
     * Public ctor, from URL.
     * @param url Location of document
     * @throws IOException If fails to read
     * @since 0.7.4
     */
    public XSDDocument(final URL url) throws IOException {
        this(new TextResource(url).toString());
    }

    /**
     * Public ctor, from file.
     * @param file Location of document
     * @throws FileNotFoundException If fails to read
     * @since 0.21
     */
    public XSDDocument(final Path file) throws FileNotFoundException {
        this(file.toFile());
    }

    /**
     * Public ctor, from file.
     * @param file Location of document
     * @throws FileNotFoundException If fails to read
     * @since 0.21
     */
    public XSDDocument(final File file) throws FileNotFoundException {
        this(new TextResource(file).toString());
    }

    /**
     * Public ctor, from URI.
     * @param uri Location of document
     * @throws IOException If fails to read
     * @since 0.15
     */
    public XSDDocument(final URI uri) throws IOException {
        this(new TextResource(uri).toString());
    }

    /**
     * Public ctor, from XSD as an input stream.
     * @param stream XSD input stream
     */
    public XSDDocument(final InputStream stream) {
        this(new TextResource(stream).toString());
    }

    /**
     * Make an instance of XSD schema without I/O exceptions.
     *
     * <p>This factory method is useful when you need to create
     * an instance of XSD schema as a static final variable. In this
     * case you can't catch an exception but this method can help, for example:
     *
     * <pre> class Foo {
     *   private static final XSD SCHEMA = XSDDocument.make(
     *     Foo.class.getResourceAsStream("my-schema.xsd")
     *   );
     * }</pre>
     *
     * @param stream Input stream
     * @return XSD schema
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static XSD make(final InputStream stream) {
        return new XSDDocument(stream);
    }

    /**
     * Make an instance of XSD schema without I/O exceptions.
     * @param url URL with content
     * @return XSD schema
     * @see #make(InputStream)
     * @since 0.7.4
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static XSD make(final URL url) {
        try {
            return new XSDDocument(url);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String toString() {
        return new XMLDocument(this.xsd).toString();
    }

    @Override
    public Collection<SAXParseException> validate(final Source xml) {
        return new XMLDocument(xml).validate(new XMLDocument(this.xsd));
    }

    /**
     * Validation error handler.
     *
     * @since 0.1
     */
    static final class ValidationHandler implements ErrorHandler {
        /**
         * Errors.
         */
        private final transient Collection<SAXParseException> errors;

        /**
         * Constructor.
         * @param errs Collection of errors
         */
        ValidationHandler(final Collection<SAXParseException> errs) {
            this.errors = errs;
        }

        @Override
        public void warning(final SAXParseException error) {
            this.errors.add(error);
        }

        @Override
        public void error(final SAXParseException error) {
            this.errors.add(error);
        }

        @Override
        public void fatalError(final SAXParseException error) {
            this.errors.add(error);
        }
    }

}
