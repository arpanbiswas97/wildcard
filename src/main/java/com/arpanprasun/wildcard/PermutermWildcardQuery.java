package com.arpanprasun.wildcard;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PrefixTermsEnum;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.index.SingleTermsEnum;

public class PermutermWildcardQuery extends MultiTermQuery{

    private Term term;

    public PermutermWildcardQuery(Term term) {
        super(term.field());
        this.term = term;
    }

    @Override
    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        String field = term.field();
        String text = term.text();
        if (text.indexOf('*') == -1) {
            return new SingleTermsEnum(terms.iterator(null), term.bytes());
        }
        else {
            if (text.charAt(0) == '*') {
                String ptext = text.substring(1, text.length()) + "$";
                return new PrefixTermsEnum(terms.iterator(null), (new Term(field, ptext)).bytes());
            }
            else if (text.charAt(text.length()-1) == '*') {
                String ptext = "$" + text.substring(0, text.length()-1);
                return new PrefixTermsEnum(terms.iterator(null), (new Term(field, ptext)).bytes());
            }
            else {
                int i = text.indexOf('*');
                String ptext = text.substring(i+1, text.length()) + "$" + text.substring(0, i);
                return new PrefixTermsEnum(terms.iterator(null), (new Term(field, ptext)).bytes());
            }
        }
    }

    @Override
    public String toString(String field) {
        return null;
    }

}
