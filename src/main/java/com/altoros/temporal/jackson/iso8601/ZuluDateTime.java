package com.altoros.temporal.jackson.iso8601;

import java.io.Serializable;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;
import org.litesoft.annotations.Significant;
import org.litesoft.annotations.SignificantOrNull;
import com.altoros.temporal.MillisecTimeSource;

/**
 * This class provides the appropriate support for handling ISO8601 formatted DateTimes which are always based on
 * <code>UTC</code>/Zulu Time Zone and when rendered to a <code>String</code> (JSON) will always produce Millisec resolution.
 * <p>
 * The methods of this class are a mixture of those found in <code>LocalDateTime</code> & <code>Instant</code>.
 * <p>
 * Notes:<br>
 * <li>The <code>LocalDateTime</code> produced by <code>toLocalDateTime()</code> is assumed to remain in the <code>UTC</code> 'time zone'.</li>
 * <li>Since the math methods are NOT supported directly, it is recommended to convert instances of this class into
 * a <code>LocalDateTime</code>, manipulate that, and then convert the changed <code>LocalDateTime</code> back into a new <code>ZuluDateTime</code>.</li>
 * <li>Should an instance of this class be created (e.g. Java Serialization) without the appropriate internal values, all references to the methods
 * of this instance will return values as if it was initialized with: <code>-999080706-05-04T03:02:01.000Z</code></li>
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class ZuluDateTime implements Comparable<ZuluDateTime>,
                                           TemporalAccessor,
                                           Serializable {
  public static final ZoneOffset UTC_ZONE_OFFSET = ZoneOffset.UTC;
  public static final ZoneId UTC_ZONE_ID = ZoneId.ofOffset( "UTC", UTC_ZONE_OFFSET );

  public static final LocalDateTime INVALID_INITIALIZATION_INDICATOR_LOCAL_DATE_TIME_VALUE = LocalDateTime
          .of( -999080706, 5, 4, 3, 2, 1 ); // -999080706-05-04T03:02:01.000Z

  public static final Instant INVALID_INITIALIZATION_INDICATOR_INSTANT_VALUE = Instant
          .ofEpochSecond( INVALID_INITIALIZATION_INDICATOR_LOCAL_DATE_TIME_VALUE.toEpochSecond( UTC_ZONE_OFFSET ) );

  public static final DateTimeFormatter RENDERING_ISO_8601_3_FACTIONAL_SEC =
          new DateTimeFormatterBuilder()
                  .appendInstant( 3 )
                  .toFormatter();

  /**
   * Serialization version.
   */
  private static final long serialVersionUID = 1L;

  private final Instant mInstant;
  private transient volatile LocalDateTime mLocalDateTime; // Don't Serialize - Note: 'volatile' supports near Singleton

  /**
   * Create a <code>ZuluDateTime</code> from the required <code>Instant</code> (note: since <code>ZuluDateTime</code>'s resolution is only to the Millisec, the <code>Instant</code> may get truncated).
   *
   * @param pInstant not null
   */
  public ZuluDateTime( @NotNull Instant pInstant ) {
    mInstant = Objects.requireNonNull( pInstant, "Instant" ).truncatedTo( ChronoUnit.MILLIS );
  }

  /**
   * Create a <code>ZuluDateTime</code> from the optional Clock.
   * <p>
   * This will query the specified clock to obtain the current time.
   * <p>
   *
   * @param pClock if null the Clock.systemUTC() will be used
   *
   * @return not null
   */
  public static ZuluDateTime now( @Nullable Clock pClock ) {
    return now( MillisecTimeSource.from( pClock ) );
  }

  /**
   * Create a <code>ZuluDateTime</code> from the optional Clock.
   * <p>
   * This will query the specified clock to obtain the current time.
   * <p>
   *
   * @param pMillisecTimeSource not null
   *
   * @return not null
   */
  public static ZuluDateTime now( @NotNull MillisecTimeSource pMillisecTimeSource ) {
    return new ZuluDateTime( Instant.ofEpochMilli(
            Objects.requireNonNull( pMillisecTimeSource, "MillisecTimeSource" ).currentTimeMillis() ) );
  }

  /**
   * Create a <code>ZuluDateTime</code> from the text such as {@code 2007-12-03T10:15:30.00Z} (note: since <code>ZuluDateTime</code>'s resolution is only to the Millisec, some portion of the fractional second may be lost).
   * <p>
   * The text is parsed using {@link DateTimeFormatter#ISO_INSTANT}.
   *
   * @param pText the text to parse, must be significant after trimming
   *
   * @return not null
   *
   * @throws DateTimeParseException if the text cannot be parsed
   */
  @NotNull
  public static ZuluDateTime parse( @Significant CharSequence pText ) {
    return new ZuluDateTime( Instant.parse( significant( pText ) ) );
  }

  // ------------ vvv of field constructors (See LocalDateTime) of & to vvv ------------

  public static ZuluDateTime of( int year, Month month, int dayOfMonth, int hour, int minute ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute ) );
  }

  public static ZuluDateTime of( int year, Month month, int dayOfMonth, int hour, int minute, int second ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute, second ) );
  }

  public static ZuluDateTime of( int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute, second, nanoOfSecond ) );
  }

  public static ZuluDateTime of( int year, int month, int dayOfMonth, int hour, int minute ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute ) );
  }

  public static ZuluDateTime of( int year, int month, int dayOfMonth, int hour, int minute, int second ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute, second ) );
  }

  public static ZuluDateTime of( int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond ) {
    return ofLocalDateTime( LocalDateTime.of( year, month, dayOfMonth, hour, minute, second, nanoOfSecond ) );
  }

  public static ZuluDateTime of( LocalDate date, LocalTime time ) {
    return ofLocalDateTime( LocalDateTime.of( date, time ) );
  }

  // ------------ ^^^ of field constructors (See LocalDateTime) of & to ^^^ ------------

  // ----------------------------- vvv JsonCreator & JsonValue vvv ---------------------

  @JsonCreator
  @Nullable
  public static ZuluDateTime jsonCreator( String pText ) {
    if ( pText != null ) {
      pText = pText.trim();
      if ( !pText.isEmpty() ) {
        return ofString( pText );
      }
    }
    return null;
  }

  @JsonValue
  @SignificantOrNull
  public String jsonValue() {
    return renderJson();
  }

  // ----------------------------- ^^^ JsonCreator & JsonValue ^^^ ---------------------

  private String renderJson() {
    return (mInstant == null) ? null : RENDERING_ISO_8601_3_FACTIONAL_SEC.format( mInstant );
  }

  // --------------------------------- vvv of & to vvv ---------------------------------

  /**
   * See parse(...)!
   */
  @NotNull
  public static ZuluDateTime ofString( @Significant String pText ) {
    return parse( pText );
  }

  @Override
  @Significant
  public String toString() {
    return renderJson();
  }

  @NotNull
  public static ZuluDateTime ofInstant( @NotNull Instant pInstant ) {
    return new ZuluDateTime( pInstant );
  }

  @NotNull
  public Instant toInstant() {
    return (mInstant != null) ? mInstant : INVALID_INITIALIZATION_INDICATOR_INSTANT_VALUE;
  }

  @NotNull
  public static ZuluDateTime ofLocalDateTime( @NotNull LocalDateTime pDateTime ) {
    return ofInstant( Objects.requireNonNull( pDateTime, "DateTime" ).toInstant( UTC_ZONE_OFFSET ) );
  }

  @NotNull
  public LocalDateTime toLocalDateTime() {
    LocalDateTime zLocalDateTime = mLocalDateTime;
    if ( zLocalDateTime == null ) {
      if ( mInstant == null ) {
        return INVALID_INITIALIZATION_INDICATOR_LOCAL_DATE_TIME_VALUE;
      }
      mLocalDateTime = zLocalDateTime = LocalDateTime.ofInstant( mInstant, UTC_ZONE_OFFSET );
    }
    return zLocalDateTime;
  }

  @NotNull
  public static ZuluDateTime ofOffsetDateTime( @NotNull OffsetDateTime pDateTime ) {
    return ofInstant( Objects.requireNonNull( pDateTime, "DateTime" ).toInstant() );
  }

  @NotNull
  public OffsetDateTime toOffsetDateTime() {
    return OffsetDateTime.of( toLocalDateTime(), UTC_ZONE_OFFSET );
  }

  @NotNull
  public static ZuluDateTime ofZonedDateTime( @NotNull ZonedDateTime pDateTime ) {
    return ofInstant( Objects.requireNonNull( pDateTime, "DateTime" ).toInstant() );
  }

  @NotNull
  public ZonedDateTime toZonedDateTime() {
    return ZonedDateTime.of( toLocalDateTime(), UTC_ZONE_ID );
  }

  // --------------------------------- ^^^ of & to ^^^ ---------------------------------

  // -------------------- vvv Field Accessors (See LocalDateTime) vvv ------------------

  public LocalDate toLocalDate() {
    return toLocalDateTime().toLocalDate();
  }

  public int getYear() {
    return toLocalDateTime().getYear();
  }

  public int getMonthValue() {
    return toLocalDateTime().getMonthValue();
  }

  public Month getMonth() {
    return toLocalDateTime().getMonth();
  }

  public int getDayOfMonth() {
    return toLocalDateTime().getDayOfMonth();
  }

  public int getDayOfYear() {
    return toLocalDateTime().getDayOfYear();
  }

  public DayOfWeek getDayOfWeek() {
    return toLocalDateTime().getDayOfWeek();
  }

  public LocalTime toLocalTime() {
    return toLocalDateTime().toLocalTime();
  }

  public int getHour() {
    return toLocalDateTime().getHour();
  }

  public int getMinute() {
    return toLocalDateTime().getMinute();
  }

  public int getSecond() {
    return toLocalDateTime().getSecond();
  }

  public int getNano() {
    return toLocalDateTime().getNano();
  }

  // -------------------- ^^^ Field Accessors (See LocalDateTime) ^^^ ------------------

  // ----------------------------- vvv TemporalAccessor vvv ----------------------------

  @Override
  public boolean isSupported( TemporalField pTemporalField ) {
    return toLocalDateTime().isSupported( pTemporalField );
  }

  @Override
  public long getLong( TemporalField pTemporalField ) {
    return toLocalDateTime().getLong( pTemporalField );
  }

  @Override
  public ValueRange range( TemporalField pTemporalField ) {
    return toLocalDateTime().range( pTemporalField );
  }

  @Override
  public int get( TemporalField pTemporalField ) {
    return toLocalDateTime().get( pTemporalField );
  }

  @Override
  public <R> R query( TemporalQuery<R> pTemporalQuery ) {
    return toLocalDateTime().query( pTemporalQuery );
  }

  // ----------------------------- ^^^ TemporalAccessor ^^^ ----------------------------

  // ------------------- vvv is... (Relative) (see LocalDateTime) vvv ------------------

  public boolean isAfter( @NotNull ZuluDateTime them ) {
    return this.toInstant().isAfter( notNullInstant( them ) );
  }

  public boolean isBefore( @NotNull ZuluDateTime them ) {
    return this.toInstant().isBefore( notNullInstant( them ) );
  }

  public boolean isEqual( @Nullable ZuluDateTime them ) {
    return equals( them );
  }

  // ------------------- ^^^ is... (Relative) (see LocalDateTime) vvv ------------------

  @NotNull
  public ZoneOffset getOffset() {
    return UTC_ZONE_OFFSET;
  }

  @NotNull
  public ZoneId getZone() {
    return UTC_ZONE_ID;
  }

  @Override
  public int compareTo( @NotNull ZuluDateTime them ) {
    return this.toInstant().compareTo( notNullInstant( them ) );
  }

  @Override
  public int hashCode() {
    return Objects.hash( mInstant );
  }

  @Override
  public boolean equals( Object them ) {
    return (this == them) ||
           ((them instanceof ZuluDateTime) && equals( (ZuluDateTime)them )); // Left to Right
  }

  public boolean equals( ZuluDateTime them ) {
    return (this == them) ||
           ((them != null) &&
            Objects.equals( this.mInstant, them.mInstant ));
  }

  @NotNull
  private static Instant notNullInstant( @Nullable ZuluDateTime them ) {
    return Objects.requireNonNull( them, "them" ).toInstant();
  }

  private static CharSequence significant( CharSequence pText ) {
    if ( pText != null ) {
      pText = trimTrailing( trimLeading( pText ) );
      if ( pText.length() != 0 ) {
        return pText;
      }
    }
    throw new IllegalArgumentException( "Insignificant Provided Text: " + format( pText ) );
  }

  private static CharSequence trimLeading( CharSequence pText ) {
    while ( (pText.length() != 0) && isWhiteSpace( pText, 0 ) ) {
      pText = pText.subSequence( 1, pText.length() );
    }
    return pText;
  }

  private static CharSequence trimTrailing( CharSequence pText ) {
    for ( int zLast = pText.length() - 1; (zLast != -1) && isWhiteSpace( pText, zLast ); zLast-- ) {
      pText = pText.subSequence( 1, zLast );
    }
    return pText;
  }

  private static boolean isWhiteSpace( CharSequence pText, int pAt ) {
    return Character.isWhitespace( pText.charAt( pAt ) );
  }

  private static String format( CharSequence pText ) {
    return (pText == null) ? "null" : ("\"" + pText + "\"");
  }
}