/*
 * Copyright (c) 2007,2008, Stephen Colebourne & Michael Nascimento Santos
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
package javax.time.calendar;

import static org.testng.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.time.calendar.field.CenturyOfEra;
import javax.time.calendar.field.DayOfMonth;
import javax.time.calendar.field.DayOfWeek;
import javax.time.calendar.field.DayOfYear;
import javax.time.calendar.field.DecadeOfCentury;
import javax.time.calendar.field.Era;
import javax.time.calendar.field.HourOfDay;
import javax.time.calendar.field.HourOfMeridiem;
import javax.time.calendar.field.MeridiemOfDay;
import javax.time.calendar.field.MilleniumOfEra;
import javax.time.calendar.field.MinuteOfDay;
import javax.time.calendar.field.MinuteOfHour;
import javax.time.calendar.field.MonthOfQuarter;
import javax.time.calendar.field.MonthOfYear;
import javax.time.calendar.field.NanoOfSecond;
import javax.time.calendar.field.QuarterOfYear;
import javax.time.calendar.field.SecondOfDay;
import javax.time.calendar.field.SecondOfMinute;
import javax.time.calendar.field.WeekOfMonth;
import javax.time.calendar.field.WeekOfWeekyear;
import javax.time.calendar.field.Weekyear;
import javax.time.calendar.field.Year;
import javax.time.calendar.field.YearOfEra;
import javax.time.calendar.format.FlexiDateTime;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test LocalDate.
 *
 * @author Michael Nascimento Santos
 * @author Stephen Colebourne
 */
@Test
public class TestLocalDate {

    private static final String MIN_YEAR_STR = Integer.toString(Year.MIN_YEAR);
    private static final String MAX_YEAR_STR = Integer.toString(Year.MAX_YEAR);
    private LocalDate TEST_2007_07_15;

    @BeforeMethod
    public void setUp() {
        TEST_2007_07_15 = LocalDate.date(2007, 7, 15);
    }

    //-----------------------------------------------------------------------
    public void test_interfaces() {
        assertTrue(TEST_2007_07_15 instanceof Calendrical);
        assertTrue(TEST_2007_07_15 instanceof Serializable);
        assertTrue(TEST_2007_07_15 instanceof Comparable);
    }

    public void test_serialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(TEST_2007_07_15);
        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(ois.readObject(), TEST_2007_07_15);
    }

    public void test_immutable() {
        Class<LocalDate> cls = LocalDate.class;
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            assertTrue(Modifier.isPrivate(field.getModifiers()));
            assertTrue(Modifier.isFinal(field.getModifiers()));
        }
    }

    //-----------------------------------------------------------------------
    public void factory_date_objects() {
        assertEquals(TEST_2007_07_15, LocalDate.date(Year.isoYear(2007), MonthOfYear.JULY, DayOfMonth.dayOfMonth(15)));
    }

    public void factory_date_objects_leapYear() {
        LocalDate test_2008_02_29 = LocalDate.date(Year.isoYear(2008), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
        assertEquals(test_2008_02_29.getYear(), Year.isoYear(2008));
        assertEquals(test_2008_02_29.getMonthOfYear(), MonthOfYear.FEBRUARY);
        assertEquals(test_2008_02_29.getDayOfMonth(), DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullYear() {
        LocalDate.date(null, MonthOfYear.JULY, DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullMonth() {
        LocalDate.date(Year.isoYear(2007), null, DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_objects_nullDay() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.JULY, null);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_objects_nonleapYear() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.FEBRUARY, DayOfMonth.dayOfMonth(29));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void factory_date_objects_dayTooBig() {
        LocalDate.date(Year.isoYear(2007), MonthOfYear.APRIL, DayOfMonth.dayOfMonth(31));
    }

    //-----------------------------------------------------------------------
    public void factory_date_intsMonth() {
        assertEquals(TEST_2007_07_15, LocalDate.date(2007, MonthOfYear.JULY, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_dayTooLow() {
        LocalDate.date(2007, MonthOfYear.JANUARY, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_dayTooHigh() {
        LocalDate.date(2007, MonthOfYear.JANUARY, 32);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_intsMonth_yearTooLow() {
        LocalDate.date(Integer.MIN_VALUE, MonthOfYear.JANUARY, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ints() {
        assertEquals(TEST_2007_07_15.getYear(), Year.isoYear(2007));
        assertEquals(TEST_2007_07_15.getMonthOfYear(), MonthOfYear.JULY);
        assertEquals(TEST_2007_07_15.getDayOfMonth(), DayOfMonth.dayOfMonth(15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooLow() {
        LocalDate.date(2007, 1, 0);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_dayTooHigh() {
        LocalDate.date(2007, 1, 32);
    }


    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooLow() {
        LocalDate.date(2007, 0, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_monthTooHigh() {
        LocalDate.date(2007, 13, 1);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_factory_date_ints_yearTooLow() {
        LocalDate.date(Integer.MIN_VALUE, 1, 1);
    }

    //-----------------------------------------------------------------------
    public void factory_date_ReadableDate() {
        assertEquals(TEST_2007_07_15, LocalDate.date(TEST_2007_07_15));
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_ReadableDate_null() {
        LocalDate.date(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void factory_date_ReadableDate_null_toDate() {
        LocalDate.date(new ReadableDate() {
            public LocalDate toLocalDate() {
                return null;
            }

            public FlexiDateTime toFlexiDateTime() {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    public void test_getChronology() {
        assertSame(ISOChronology.INSTANCE, TEST_2007_07_15.getChronology());
    }

    //-----------------------------------------------------------------------
    public void test_isSupported() {
        assertTrue(TEST_2007_07_15.isSupported(Era.RULE));
        assertTrue(TEST_2007_07_15.isSupported(MilleniumOfEra.RULE));
        assertTrue(TEST_2007_07_15.isSupported(CenturyOfEra.RULE));
        assertTrue(TEST_2007_07_15.isSupported(DecadeOfCentury.RULE));
        assertTrue(TEST_2007_07_15.isSupported(Year.RULE));
        assertTrue(TEST_2007_07_15.isSupported(YearOfEra.RULE));
        assertTrue(TEST_2007_07_15.isSupported(QuarterOfYear.RULE));
        assertTrue(TEST_2007_07_15.isSupported(MonthOfYear.RULE));
        assertTrue(TEST_2007_07_15.isSupported(MonthOfQuarter.RULE));
        assertTrue(TEST_2007_07_15.isSupported(DayOfMonth.RULE));
        assertTrue(TEST_2007_07_15.isSupported(DayOfWeek.RULE));
        assertTrue(TEST_2007_07_15.isSupported(DayOfYear.RULE));
        assertTrue(TEST_2007_07_15.isSupported(WeekOfMonth.RULE));
        assertTrue(TEST_2007_07_15.isSupported(WeekOfWeekyear.RULE));
        assertTrue(TEST_2007_07_15.isSupported(Weekyear.RULE));

        assertFalse(TEST_2007_07_15.isSupported(HourOfDay.RULE));
        assertFalse(TEST_2007_07_15.isSupported(MinuteOfHour.RULE));
        assertFalse(TEST_2007_07_15.isSupported(MinuteOfDay.RULE));
        assertFalse(TEST_2007_07_15.isSupported(SecondOfMinute.RULE));
        assertFalse(TEST_2007_07_15.isSupported(SecondOfDay.RULE));
        assertFalse(TEST_2007_07_15.isSupported(NanoOfSecond.RULE));
        assertFalse(TEST_2007_07_15.isSupported(HourOfMeridiem.RULE));
        assertFalse(TEST_2007_07_15.isSupported(MeridiemOfDay.RULE));
    }

    public void test_get() {
        assertEquals(TEST_2007_07_15.get(Era.RULE), Era.AD.getValue());
        assertEquals(TEST_2007_07_15.get(MilleniumOfEra.RULE), TEST_2007_07_15.getYear().getMilleniumOfEra());
        assertEquals(TEST_2007_07_15.get(CenturyOfEra.RULE), TEST_2007_07_15.getYear().getCenturyOfEra());
        assertEquals(TEST_2007_07_15.get(DecadeOfCentury.RULE), TEST_2007_07_15.getYear().getDecadeOfCentury());
        assertEquals(TEST_2007_07_15.get(Year.RULE), TEST_2007_07_15.getYear().getValue());
        assertEquals(TEST_2007_07_15.get(YearOfEra.RULE), TEST_2007_07_15.getYear().getYearOfEra());
        assertEquals(TEST_2007_07_15.get(QuarterOfYear.RULE), TEST_2007_07_15.getMonthOfYear().getQuarterOfYear().getValue());
        assertEquals(TEST_2007_07_15.get(MonthOfYear.RULE), TEST_2007_07_15.getMonthOfYear().getValue());
        assertEquals(TEST_2007_07_15.get(MonthOfQuarter.RULE), TEST_2007_07_15.getMonthOfYear().getMonthOfQuarter());
        assertEquals(TEST_2007_07_15.get(DayOfMonth.RULE), TEST_2007_07_15.getDayOfMonth().getValue());
        assertEquals(TEST_2007_07_15.get(DayOfWeek.RULE), TEST_2007_07_15.getDayOfWeek().getValue());
        assertEquals(TEST_2007_07_15.get(DayOfYear.RULE), TEST_2007_07_15.getDayOfYear().getValue());
        assertEquals(TEST_2007_07_15.get(WeekOfMonth.RULE), WeekOfMonth.weekOfMonth(TEST_2007_07_15).getValue());
        assertEquals(TEST_2007_07_15.get(WeekOfWeekyear.RULE), WeekOfWeekyear.weekOfWeekyear(TEST_2007_07_15).getValue());
        assertEquals(TEST_2007_07_15.get(Weekyear.RULE), Weekyear.weekyear(TEST_2007_07_15).getValue());
    }

    @Test(expectedExceptions=UnsupportedCalendarFieldException.class)
    public void test_get_unsupported() {
        TEST_2007_07_15.get(HourOfDay.RULE);
    }

    //-----------------------------------------------------------------------
    @DataProvider(name="sampleDates")
    Object[][] provider_sampleDates() {
        return new Object[][] {
            {2008, 7, 5},
            {2007, 7, 5},
            {2006, 7, 5},
            {2005, 7, 5},
            {2004, 1, 1},
            {-1, 1, 2},
        };
    }

    //-----------------------------------------------------------------------
    // get*()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_getYearMonth(int y, int m, int d) {
        assertEquals(LocalDate.date(y, m, d).getYearMonth(), YearMonth.yearMonth(y, m));
    }

    @Test(dataProvider="sampleDates")
    public void test_getMonthDay(int y, int m, int d) {
        assertEquals(LocalDate.date(y, m, d).getMonthDay(), MonthDay.monthDay(m, d));
    }

    @Test(dataProvider="sampleDates")
    public void test_get(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        assertEquals(a.getYear(), Year.isoYear(y));
        assertEquals(a.getMonthOfYear(), MonthOfYear.monthOfYear(m));
        assertEquals(a.getDayOfMonth(), DayOfMonth.dayOfMonth(d));
    }

    @Test(dataProvider="sampleDates")
    public void test_getDOY(int y, int m, int d) {
        Year year = Year.isoYear(y);
        LocalDate a = LocalDate.date(y, m, d);
        int total = 0;
        for (int i = 1; i < m; i++) {
            total += MonthOfYear.monthOfYear(i).lengthInDays(year);
        }
        int doy = total + d;
        assertEquals(a.getDayOfYear(), DayOfYear.dayOfYear(doy));
    }

    //-----------------------------------------------------------------------
    // with()
    //-----------------------------------------------------------------------
    public void test_with() {
        DateAdjustor dateAdjustor = DateAdjustors.lastDayOfMonth();
        assertEquals(TEST_2007_07_15.with(dateAdjustor), dateAdjustor.adjustDate(TEST_2007_07_15));
    }

    @Test(expectedExceptions=IllegalArgumentException.class)
    public void test_with_null_adjustDate() {
        TEST_2007_07_15.with(new DateAdjustor() {
            public LocalDate adjustDate(LocalDate date) {
                return null;
            }
        });
    }

    //-----------------------------------------------------------------------
    // withYear()
    //-----------------------------------------------------------------------
    public void test_withYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_withYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1);
    }

    public void test_withYear_int_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).withYear(2007);
        LocalDate expected = LocalDate.date(2007, 2, 28);
        assertEquals(t, expected);
    }

    public void test_withYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withYear(2008, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_withYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withYear(2007, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }
    
    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withYear(Year.MIN_YEAR - 1, DateResolvers.nextValid());
    }

    public void test_withYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).withYear(2007, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 3, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.date(2008, 2, 29).withYear(2007, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withMonthOfYear()
    //-----------------------------------------------------------------------
    public void test_withMonthOfYear_int_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1);
        assertEquals(t, LocalDate.date(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_invalid() {
        TEST_2007_07_15.withMonthOfYear(13);
    }

    public void test_withMonthOfYear_int_adjustDay() {
        LocalDate t = LocalDate.date(2007, 12, 31).withMonthOfYear(11);
        LocalDate expected = LocalDate.date(2007, 11, 30);
        assertEquals(t, expected);
    }

    public void test_withMonthOfYear_int_DateResolver_normal() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(1, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 1, 15));
    }

    public void test_withMonthOfYear_int_DateResolver_noChange() {
        LocalDate t = TEST_2007_07_15.withMonthOfYear(7, DateResolvers.strict());
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_invalid() {
        TEST_2007_07_15.withMonthOfYear(13, DateResolvers.nextValid());
    }

    public void test_withMonthOfYear_int_DateResolver_adjustDay() {
        LocalDate t = LocalDate.date(2007, 12, 31).withMonthOfYear(11, DateResolvers.nextValid());
        LocalDate expected = LocalDate.date(2007, 12, 1);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withMonthOfYear_int_DateResolver_adjustDay_invalid() {
        LocalDate.date(2007, 12, 31).withMonthOfYear(11, DateResolvers.strict());
    }

    //-----------------------------------------------------------------------
    // withDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withDayOfMonth_normal() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(1);
        assertEquals(t, LocalDate.date(2007, 7, 1));
    }

    public void test_withDayOfMonth_noChange() {
        LocalDate t = TEST_2007_07_15.withDayOfMonth(15);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_withDayOfMonth_invalid() {
        LocalDate.date(2007, 11, 30).withDayOfMonth(31);
    }

    //-----------------------------------------------------------------------
    // withLastDayOfMonth()
    //-----------------------------------------------------------------------
    public void test_withLastDayOfMonth_leap() {
        Year year = Year.isoYear(2008);
        for (MonthOfYear month : MonthOfYear.values()) {
            LocalDate t = LocalDate.date(2008, month, 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.getLastDayOfMonth(year));
        }
    }

    public void test_withLastDayOfMonth_standard() {
        Year year = Year.isoYear(2007);
        for (MonthOfYear month : MonthOfYear.values()) {
            LocalDate t = LocalDate.date(2007, month, 1).withLastDayOfMonth();
            assertEquals(t.getDayOfMonth(), month.getLastDayOfMonth(year));
        }
    }

    public void test_withLastDayOfMonth_noChange() {
        LocalDate t = LocalDate.date(2008, 10, 31).withLastDayOfMonth();
        assertEquals(t, LocalDate.date(2008, 10, 31));
    }

    //-----------------------------------------------------------------------
    // plusYears()
    //-----------------------------------------------------------------------
    public void test_plusYears_normal() {
        LocalDate t = TEST_2007_07_15.plusYears(1);
        assertEquals(t, LocalDate.date(2008, 7, 15));
    }

    public void test_plusYears_noChange() {
        LocalDate t = TEST_2007_07_15.plusYears(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusYears_negative() {
        LocalDate t = TEST_2007_07_15.plusYears(-1);
        assertEquals(t, LocalDate.date(2006, 7, 15));
    }

    public void test_plusYears_adjustDay() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusYears(1);
        LocalDate expected = LocalDate.date(2009, 2, 28);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusYears_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 1, 1).plusYears(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusYears_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusYears(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // plusMonths()
    //-----------------------------------------------------------------------
    public void test_plusMonths_normal() {
        LocalDate t = TEST_2007_07_15.plusMonths(1);
        assertEquals(t, LocalDate.date(2007, 8, 15));
    }

    public void test_plusMonths_noChange() {
        LocalDate t = TEST_2007_07_15.plusMonths(0);
        assertEquals(t, LocalDate.date(2007, 7, 15));
    }

    public void test_plusMonths_negative() {
        LocalDate t = TEST_2007_07_15.plusMonths(-1);
        assertEquals(t, LocalDate.date(2007, 6, 15));
    }

    public void test_plusMonths_negativeAcrossYear() {
        LocalDate t = TEST_2007_07_15.plusMonths(-7);
        assertEquals(t, LocalDate.date(2006, 12, 15));
    }

    public void test_plusMonths_adjustDayFromLeapYear() {
        LocalDate t = LocalDate.date(2008, 2, 29).plusMonths(12);
        LocalDate expected = LocalDate.date(2009, 2, 28);
        assertEquals(t, expected);
    }

    public void test_plusMonths_adjustDayFromMonthLength() {
        LocalDate t = LocalDate.date(2007, 3, 31).plusMonths(1);
        LocalDate expected = LocalDate.date(2007, 4, 30);
        assertEquals(t, expected);
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusMonths_invalidTooLarge() {
        try {
            LocalDate.date(Year.MAX_YEAR, 12, 1).plusMonths(1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MAX_YEAR) + 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    @Test(expectedExceptions=IllegalCalendarFieldValueException.class)
    public void test_plusMonths_invalidTooSmall() {
        try {
            LocalDate.date(Year.MIN_YEAR, 1, 1).plusMonths(-1);
            fail();
        } catch (IllegalCalendarFieldValueException ex) {
            String actual = Long.toString(((long) Year.MIN_YEAR) - 1);
            assertEquals(ex.getMessage(), "Illegal value for Year field, value " + actual +
                " is not in the range " + MIN_YEAR_STR + " to " + MAX_YEAR_STR);
            throw ex;
        }
    }

    //-----------------------------------------------------------------------
    // compareTo()
    //-----------------------------------------------------------------------
    public void test_comparisons() {
        doTest_comparisons_LocalDate(
            LocalDate.date(Year.MIN_YEAR, 1, 1),
            LocalDate.date(Year.MIN_YEAR, 12, 31),
            LocalDate.date(-1, 1, 1),
            LocalDate.date(-1, 12, 31),
            LocalDate.date(0, 1, 1),
            LocalDate.date(0, 12, 31),
            LocalDate.date(1, 1, 1),
            LocalDate.date(1, 12, 31),
            LocalDate.date(2006, 1, 1),
            LocalDate.date(2006, 12, 31),
            LocalDate.date(2007, 1, 1),
            LocalDate.date(2007, 12, 31),
            LocalDate.date(2008, 1, 1),
            LocalDate.date(2008, 2, 29),
            LocalDate.date(2008, 12, 31),
            LocalDate.date(Year.MAX_YEAR, 1, 1),
            LocalDate.date(Year.MAX_YEAR, 12, 31)
        );
    }

    void doTest_comparisons_LocalDate(LocalDate... LocalDates) {
        for (int i = 0; i < LocalDates.length; i++) {
            LocalDate a = LocalDates[i];
            for (int j = 0; j < LocalDates.length; j++) {
                LocalDate b = LocalDates[j];
                if (i < j) {
                    assertTrue(a.compareTo(b) < 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), true, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else if (i > j) {
                    assertTrue(a.compareTo(b) > 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), true, a + " <=> " + b);
                    assertEquals(a.equals(b), false, a + " <=> " + b);
                } else {
                    assertEquals(a.compareTo(b), 0, a + " <=> " + b);
                    assertEquals(a.isBefore(b), false, a + " <=> " + b);
                    assertEquals(a.isAfter(b), false, a + " <=> " + b);
                    assertEquals(a.equals(b), true, a + " <=> " + b);
                }
            }
        }
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_compareTo_ObjectNull() {
        TEST_2007_07_15.compareTo(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isBefore_ObjectNull() {
        TEST_2007_07_15.isBefore(null);
    }

    @Test(expectedExceptions=NullPointerException.class)
    public void test_isAfter_ObjectNull() {
        TEST_2007_07_15.isAfter(null);
    }

    @Test(expectedExceptions=ClassCastException.class)
    @SuppressWarnings("unchecked")
    public void compareToNonLocalDate() {
       Comparable c = TEST_2007_07_15;
       c.compareTo(new Object());
    }

    //-----------------------------------------------------------------------
    // equals()
    //-----------------------------------------------------------------------
    @Test(dataProvider="sampleDates")
    public void test_equals_true(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d);
        assertEquals(a.equals(b), true);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_year_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y + 1, m, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_month_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m + 1, d);
        assertEquals(a.equals(b), false);
    }
    @Test(dataProvider="sampleDates")
    public void test_equals_false_day_differs(int y, int m, int d) {
        LocalDate a = LocalDate.date(y, m, d);
        LocalDate b = LocalDate.date(y, m, d + 1);
        assertEquals(a.equals(b), false);
    }

    public void test_equals_itself_true() {
        assertEquals(TEST_2007_07_15.equals(TEST_2007_07_15), true);
    }

    public void test_equals_string_false() {
        assertEquals(TEST_2007_07_15.equals("2007-07-15"), false);
    }

    //-----------------------------------------------------------------------
    // toString()
    //-----------------------------------------------------------------------
    @DataProvider(name="sampleToString")
    Object[][] provider_sampleToString() {
        return new Object[][] {
            {2008, 7, 5, "2008-07-05"},
            {2007, 12, 31, "2007-12-31"},
            {999, 12, 31, "0999-12-31"},
            {-1, 1, 2, "-0001-01-02"},
        };
    }

    @Test(dataProvider="sampleToString")
    public void test_toString(int y, int m, int d, String expected) {
        LocalDate t = LocalDate.date(y, m, d);
        String str = t.toString();
        assertEquals(str, expected);
    }

}
