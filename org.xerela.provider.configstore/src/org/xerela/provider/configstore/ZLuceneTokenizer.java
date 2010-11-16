/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is Ziptie Client Framework.
 * 
 * The Initial Developer of the Original Code is AlterPoint.
 * Portions created by AlterPoint are Copyright (C) 2006,
 * AlterPoint, Inc. All Rights Reserved.
 * 
 * Contributor(s):
 */

package org.xerela.provider.configstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;

/**
 * ZLuceneTokenizer
 */
public class ZLuceneTokenizer extends Tokenizer
{
    private static final boolean[] IGNORE_CHAR;
    private static final int TOP_OF_RANGE = 255;

    private StringBuilder currentToken;
    private Reader reader;
    private int offset;

    static
    {
        IGNORE_CHAR = new boolean[TOP_OF_RANGE];
        IGNORE_CHAR[';'] = true;
        IGNORE_CHAR[' '] = true;
        IGNORE_CHAR['{'] = true;
        IGNORE_CHAR['}'] = true;
        IGNORE_CHAR['['] = true;
        IGNORE_CHAR[']'] = true;
        IGNORE_CHAR['<'] = true;
        IGNORE_CHAR['>'] = true;
        IGNORE_CHAR['('] = true;
        IGNORE_CHAR[')'] = true;
        IGNORE_CHAR['!'] = true;
        IGNORE_CHAR['`'] = true;
        IGNORE_CHAR['~'] = true;
        IGNORE_CHAR['#'] = true;
        IGNORE_CHAR['$'] = true;
        IGNORE_CHAR['%'] = true;
        IGNORE_CHAR['&'] = true;
        IGNORE_CHAR['*'] = true;
        IGNORE_CHAR['='] = true;
        IGNORE_CHAR['+'] = true;
        IGNORE_CHAR['"'] = true;
        IGNORE_CHAR[','] = true;
        IGNORE_CHAR['?'] = true;
        IGNORE_CHAR['|'] = true;
        IGNORE_CHAR['/'] = true;
        IGNORE_CHAR['\r'] = true;
        IGNORE_CHAR['\n'] = true;
        IGNORE_CHAR['\t'] = true;
        IGNORE_CHAR['\''] = true;
        IGNORE_CHAR['\\'] = true;
    }

    /**
     * Constructor.
     *
     * @param reader a reader
     */
    public ZLuceneTokenizer(Reader reader)
    {
        super(reader);

        this.reader = (reader instanceof BufferedReader ? reader : new BufferedReader(reader));
        this.currentToken = new StringBuilder();
    }

    /** {@inheritDoc} */
    @Override
    public Token next(Token token) throws IOException
    {
        currentToken.setLength(0);

        int startOffset = offset;
        int endOffset = offset;
        boolean tokenStarted = false;
        while (true)
        {
            int c = reader.read();
            if (c == -1)
            {
                endOffset = offset - 1;
                break;
            }

            if (IGNORE_CHAR[c])
            {
                if (tokenStarted)
                {
                    endOffset = offset;
                    ++offset;
                    break;
                }

                ++offset;
                continue;
            }

            if (!tokenStarted)
            {
                startOffset = offset;
                tokenStarted = true;
            }

            currentToken.append((char) c);
            ++offset;
        }

        if (currentToken.length() == 0)
        {
            return null;
        }

        token.clear();
        token.setTermText(currentToken.toString());
        token.setTermLength(currentToken.length());
        token.setStartOffset(startOffset);
        token.setEndOffset(endOffset);

        return token;
    }
}
