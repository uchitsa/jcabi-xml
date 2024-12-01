/*
 * Copyright (c) 2012-2024, jcabi.com
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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.stream.StreamSource;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;

/**
 * Saxon XML document.
 *
 * <p>Objects of this class are immutable, but NOT thread-safe.
 *
 * @since 0.28
 */
public final class SaxonDocument implements XML {

    /**
     * Saxon processor.
     */
    private static final Processor SAXON = new Processor(false);

    /**
     * Saxon document builder.
     */
    private static final DocumentBuilder DOC_BUILDER = SaxonDocument.SAXON.newDocumentBuilder();

    /**
     * Saxon XPath compiler.
     */
    private static final XPathCompiler XPATH_COMPILER = SaxonDocument.SAXON.newXPathCompiler();

    /**
     * Exception message for unsupported methods.
     */
    private static final String UNSUPPORTED =
        "The %s method is not supported yet. You can use XMLDocument instead or if you need to use Saxon specific features, you can open an issue at https://github.com/jcabi/jcabi-xml";

    /**
     * Saxon XML document node.
     */
    private final XdmNode xdm;

    /**
     * Public constructor from XML as string text.
     * @param text XML document body.
     * @since 0.28.0
     */
    public SaxonDocument(final String text) {
        this(SaxonDocument.node(text));
    }

    /**
     * Public constructor from XML as byte array.
     * @param data XML document body as byte array.
     * @since 0.28.1
     */
    public SaxonDocument(final byte[] data) {
        this(SaxonDocument.node(new String(data, StandardCharsets.UTF_8)));
    }

    /**
     * Public constructor from XML saved in a filesystem.
     * @param path Path to XML file in a filesystem.
     * @since 0.28.1
     */
    public SaxonDocument(final Path path) {
        this(path.toFile());
    }

    /**
     * Public constructor from XML saved in a filesystem.
     * @param file XML file in a filesystem.
     * @since 0.28.1
     */
    public SaxonDocument(final File file) {
        this(SaxonDocument.node(new StreamSource(file)));
    }

    /**
     * Public constructor from XML reached by URL.
     * @param url URL of XML document.
     * @throws IOException If fails.
     * @since 0.28.1
     */
    public SaxonDocument(final URL url) throws IOException {
        this(SaxonDocument.node(new TextResource(url).toString()));
    }

    /**
     * Public constructor from XML reached by URI.
     * @param uri URI of XML document.
     * @throws IOException If fails.
     * @since 0.28.1
     */
    public SaxonDocument(final URI uri) throws IOException {
        this(SaxonDocument.node(new TextResource(uri).toString()));
    }

    /**
     * Public constructor from XML as input stream.
     * @param stream Input stream with XML document.
     * @since 0.28.1
     */
    public SaxonDocument(final InputStream stream) {
        this(SaxonDocument.node(new StreamSource(stream)));
    }

    /**
     * Public constructor from Saxon XML document node.
     * @param xml Saxon XML document node.
     * @since 0.28.0
     */
    public SaxonDocument(final XdmNode xml) {
        this.xdm = xml;
    }

    @Override
    public List<String> xpath(final String query) {
        try {
            final XPathSelector selector = SaxonDocument.XPATH_COMPILER.compile(query).load();
            selector.setContextItem(this.xdm);
            return selector.evaluate()
                .stream()
                .map(XdmItem::getStringValue)
                .collect(Collectors.toList());
        } catch (final SaxonApiException exception) {
            throw new IllegalArgumentException(
                String.format("Can't evaluate the '%s' XPath query with Saxon API", query),
                exception
            );
        }
    }

    @Override
    public List<XML> nodes(final String query) {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "nodes")
        );
    }

    @Override
    public XML registerNs(final String prefix, final Object uri) {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "registerNs")
        );
    }

    @Override
    public XML merge(final NamespaceContext context) {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "merge")
        );
    }

    @Override
    public Node node() {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "node")
        );
    }

    @Override
    public Collection<SAXParseException> validate() {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "validate")
        );
    }

    @Override
    public Collection<SAXParseException> validate(final XML xsd) {
        throw new UnsupportedOperationException(
            String.format(SaxonDocument.UNSUPPORTED, "validate")
        );
    }

    /**
     * Build Saxon XML document node from XML string text.
     * @param text XML string text.
     * @return Saxon XML document node.
     */
    private static XdmNode node(final String text) {
        return SaxonDocument.node(new StreamSource(new StringReader(text)));
    }

    /**
     * Build Saxon XML document node from XML source.
     * @param source XML.
     * @return Saxon XML document node.
     */
    private static XdmNode node(final StreamSource source) {
        try {
            return SaxonDocument.DOC_BUILDER.build(source);
        } catch (final SaxonApiException exception) {
            throw new IllegalArgumentException(
                String.format("SaxonDocument can't parse XML from source '%s'", source),
                exception
            );
        }
    }
}
