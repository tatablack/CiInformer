/*
 * Copyright (c) 2004-2010, Kohsuke Kawaguchi
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright notice, this list of
 *       conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.tatablack.ci.informer.serialization;

import java.io.IOException;
import java.io.Writer;

import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.DataWriter;

/**
 * JSON writer.
 *
 * @author Kohsuke Kawaguchi
 */
public class JSONDataWriter implements DataWriter {
    protected boolean needComma;
    protected final Writer out;

    public JSONDataWriter(Writer out) throws IOException {
        this.out = out;
    }

    public JSONDataWriter(StaplerResponse rsp) throws IOException {
        out = rsp.getWriter();
    }

    public void name(String name) throws IOException {
        comma();
        out.write('"'+name+"\":");
        needComma = false;
    }

    protected void data(String v) throws IOException {
        comma();
        out.write(v);
    }

    protected void comma() throws IOException {
        if(needComma) out.write(',');
        needComma = true;
    }

    public void valuePrimitive(Object v) throws IOException {
        data(v.toString());
    }

    public void value(String v) throws IOException {
        StringBuilder buf = new StringBuilder(v.length());
        buf.append('\"');
        for( int i=0; i<v.length(); i++ ) {
            char c = v.charAt(i);
            switch(c) {
            case '"':   buf.append("\\\"");break;
            case '\\':  buf.append("\\\\");break;
            case '\n':  buf.append("\\n");break;
            case '\r':  buf.append("\\r");break;
            case '\t':  buf.append("\\t");break;
            default:    buf.append(c);break;
            }
        }
        buf.append('\"');
        data(buf.toString());
    }

    public void valueNull() throws IOException {
        data("null");
    }

    public void startArray() throws IOException {
        comma();
        out.write('[');
        needComma = false;
    }

    public void endArray() throws IOException {
        out.write(']');
        needComma = true;
    }

    public void startObject() throws IOException {
        comma();
        out.write('{');
        needComma=false;
    }

    public void endObject() throws IOException {
        out.write('}');
        needComma=true;
    }
}