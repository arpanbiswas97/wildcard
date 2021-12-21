package com.arpanprasun.wildcard;

import java.io.IOException;
import java.util.Stack;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

public class PermutermTokenFilter extends TokenFilter{
    private CharTermAttribute termAttr;
    private PositionIncrementAttribute posIncAttr;
    private AttributeSource.State current;
    private TokenStream input;
    private Stack<char[]> permuterms;

    protected PermutermTokenFilter(TokenStream input) {
        super(input);
        this.termAttr = addAttribute(CharTermAttribute.class);
        this.posIncAttr = addAttribute(PositionIncrementAttribute.class);
        this.input = input;
        this.permuterms = new Stack<char[]>();
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (permuterms.size() > 0) {
            char[] permuterm = permuterms.pop();
            restoreState(current);
            termAttr.copyBuffer(permuterm, 0, permuterm.length);
            posIncAttr.setPositionIncrement(0);
            return true;
        }
        if (! input.incrementToken()) {
            return false;
        }
        if (addPermuterms()) {
            current = captureState();
        }
        return true;
    }

    private boolean addPermuterms() {
        char[] buf = termAttr.buffer();
        char[] obuf = new char[termAttr.length() + 1];
        for (int i = 0; i < obuf.length - 1; i++) {
            obuf[i] = buf[i];
        }
        obuf[obuf.length-1] = '$';
        for (int i = 0; i < obuf.length; i++) {
            char[] permuterm = getPermutermAt(obuf, i);
            permuterms.push(permuterm);
        }
        return true;
    }

    private char[] getPermutermAt(char[] obuf, int pos) {
        char[] pbuf = new char[obuf.length];
        int curr = pos;
        for (int i = 0; i < pbuf.length; i++) {
            pbuf[i] = obuf[curr];
            curr++;
            if (curr == obuf.length) {
                curr = 0;
            }
        }
        return pbuf;
    }
}
