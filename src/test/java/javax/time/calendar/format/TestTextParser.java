/*
 * Copyright (c) 2008, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package javax.time.calendar.format;

import static org.testng.Assert.*;

import java.util.Locale;

import javax.time.calendar.DateTimeFieldRule;
import javax.time.calendar.ISOChronology;
import javax.time.calendar.format.DateTimeFormatterBuilder.TextStyle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test TextPrinterParser.
 *
 * @author Stephen Colebourne
 */
@Test
public class TestTextParser {

    private static final DateTimeFieldRule RULE_DOW = ISOChronology.dayOfWeekRule();
    private static final DateTimeFieldRule RULE_DOM = ISOChronology.dayOfMonthRule();
    private static final DateTimeFieldRule RULE_MOY = ISOChronology.monthOfYearRule();

    private DateTimeFormatSymbols symbols;

    @BeforeMethod
    public void setUp() {
        symbols = DateTimeFormatSymbols.getInstance(Locale.ENGLISH);
    }

    //-----------------------------------------------------------------------
    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullContext() throws Exception {
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.parse((DateTimeParseContext) null, "Monday", 0);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_print_nullText() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOW, 2);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.parse(context, (String) null, 0);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooSmall() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOW, 2);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.parse(context, "Monday", -1);
    }

    @Test(expectedExceptions=IndexOutOfBoundsException.class)
    public void test_print_invalidPositionTooBig() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOW, 2);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        pp.parse(context, "Monday", 7);
    }

    //-----------------------------------------------------------------------
    public void test_parse_replaceContextValue() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        context.setFieldValue(RULE_DOW, 2);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 0);
        assertEquals(newPos, 6);
        assertEquals(context.getFieldValueMapValue(RULE_DOW), 1);
    }

    public void test_parse_midStr() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        int newPos = pp.parse(context, "XxxMondayXxx", 3);
        assertEquals(newPos, 9);
        assertEquals(context.getFieldValueMapValue(RULE_DOW), 1);
    }

    public void test_parse_remainderIgnored() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.SHORT);
        int newPos = pp.parse(context, "Wednesday", 0);
        assertEquals(newPos, 3);
        assertEquals(context.getFieldValueMapValue(RULE_DOW), 3);
    }

    //-----------------------------------------------------------------------
    public void test_parse_noMatch1() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        int newPos = pp.parse(context, "Munday", 0);
        assertEquals(newPos, ~0);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOW), false);
    }

    public void test_parse_noMatch2() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 3);
        assertEquals(newPos, ~3);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOW), false);
    }

    public void test_parse_noMatch_atEnd() throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(RULE_DOW, TextStyle.FULL);
        int newPos = pp.parse(context, "Monday", 6);
        assertEquals(newPos, ~6);
        assertEquals(context.toCalendrical().getFieldMap().contains(RULE_DOW), false);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="parse")
    Object[][] provider_dow() {
        return new Object[][] {
            {RULE_DOW, TextStyle.FULL, 1, "Monday"},
            {RULE_DOW, TextStyle.FULL, 2, "Tuesday"},
            {RULE_DOW, TextStyle.FULL, 3, "Wednesday"},
            {RULE_DOW, TextStyle.FULL, 4, "Thursday"},
            {RULE_DOW, TextStyle.FULL, 5, "Friday"},
            {RULE_DOW, TextStyle.FULL, 6, "Saturday"},
            {RULE_DOW, TextStyle.FULL, 7, "Sunday"},
            
            {RULE_DOW, TextStyle.SHORT, 1, "Mon"},
            {RULE_DOW, TextStyle.SHORT, 2, "Tue"},
            {RULE_DOW, TextStyle.SHORT, 3, "Wed"},
            {RULE_DOW, TextStyle.SHORT, 4, "Thu"},
            {RULE_DOW, TextStyle.SHORT, 5, "Fri"},
            {RULE_DOW, TextStyle.SHORT, 6, "Sat"},
            {RULE_DOW, TextStyle.SHORT, 7, "Sun"},
            
            {RULE_DOM, TextStyle.FULL, 1, "1"},
            {RULE_DOM, TextStyle.FULL, 2, "2"},
            {RULE_DOM, TextStyle.FULL, 3, "3"},
            {RULE_DOM, TextStyle.FULL, 28, "28"},
            {RULE_DOM, TextStyle.FULL, 29, "29"},
            {RULE_DOM, TextStyle.FULL, 30, "30"},
            {RULE_DOM, TextStyle.FULL, 31, "31"},
            
            {RULE_DOM, TextStyle.SHORT, 1, "1"},
            {RULE_DOM, TextStyle.SHORT, 2, "2"},
            {RULE_DOM, TextStyle.SHORT, 3, "3"},
            {RULE_DOM, TextStyle.SHORT, 28, "28"},
            {RULE_DOM, TextStyle.SHORT, 29, "29"},
            {RULE_DOM, TextStyle.SHORT, 30, "30"},
            {RULE_DOM, TextStyle.SHORT, 31, "31"},
            
            {RULE_MOY, TextStyle.FULL, 1, "January"},
            {RULE_MOY, TextStyle.FULL, 12, "December"},
            
            {RULE_MOY, TextStyle.SHORT, 1, "Jan"},
            {RULE_MOY, TextStyle.SHORT, 12, "Dec"},
       };
    }

    @Test(dataProvider="parse") 
    public void test_parse(DateTimeFieldRule rule, TextStyle style, int dow, String input) throws Exception {
        DateTimeParseContext context = new DateTimeParseContext(symbols);
        TextPrinterParser pp = new TextPrinterParser(rule, style);
        int newPos = pp.parse(context, input, 0);
        assertEquals(newPos, input.length());
        assertEquals(context.getFieldValueMapValue(rule), dow);
    }

}
