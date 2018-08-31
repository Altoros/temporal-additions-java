package com.altoros.temporal.iso8601;

import java.io.IOException;

import com.altoros.temporal.MillisecTimeSource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ZuluDateTimeTest {
  @Test
  public void test_JsonRTthruParseAndToString() {
    MillisecTimeSource zSource = MillisecTimeSource.SYSTEM;

    ZuluDateTime zInstance = ZuluDateTime.now( zSource );
    String zISO8601 = zInstance.toString();
    ZuluDateTime zRT = ZuluDateTime.parse( zISO8601 );
    Assert.assertEquals( zISO8601, zInstance, zRT );
    String zRTasString = zRT.toString();
    Assert.assertEquals( zISO8601, zRTasString );
//    System.out.println( zISO8601 );

    checkRTdirect( "2011-01-16T00:00:00Z", "2011-01-16T00:00:00.000Z" );
    checkRTdirect( "2011-01-16T00:00:00.1Z", "2011-01-16T00:00:00.100Z" );
    checkRTdirect( "2011-01-16T00:00:00.12Z", "2011-01-16T00:00:00.120Z" );
    checkRTdirect( "2011-01-16T00:00:00.123Z", "2011-01-16T00:00:00.123Z" );
    checkRTdirect( "2011-01-16T00:00:00.1234Z", "2011-01-16T00:00:00.123Z" );
  }

  private void checkRTdirect( String pInISO8601, String pOutISO8601 ) {
    ZuluDateTime zInstance = ZuluDateTime.parse( pInISO8601 );
    String zActual = zInstance.toString();
    Assert.assertEquals( pOutISO8601, zActual );
    if ( !pInISO8601.equals( pOutISO8601 ) ) {
      ZuluDateTime zRT = ZuluDateTime.parse( zActual );
      Assert.assertEquals( "parsing: " + pInISO8601 + " != " + pOutISO8601, zRT, zInstance );
    }
  }

  @SuppressWarnings("WeakerAccess")
  public static class SimpleTimeCarrier {
    private ZuluDateTime dt;

    public ZuluDateTime getDt() {
      return dt;
    }

    public void setDt( ZuluDateTime pDt ) {
      dt = pDt;
    }

    public SimpleTimeCarrier withDT( ZuluDateTime pDt ) {
      setDt( pDt );
      return this;
    }
  }

  @SuppressWarnings("WeakerAccess")
  public static abstract class BeanJsonConverter<T> {
    private final String mActualConverterName;

    public BeanJsonConverter( Class pActualConverterClass ) {
      mActualConverterName = pActualConverterClass.getSimpleName();
    }

    public String getActualConverterName() {
      return mActualConverterName;
    }

    abstract public String toJson( T pInstance );

    abstract public T fromJson( String pJson );
  }

  @Test
  public void test_JsonRTviaJackson() {
    ObjectMapper zMapper = new ObjectMapper();
    checkWith( new BeanJsonConverter<SimpleTimeCarrier>( zMapper.getClass() ) {
      @Override
      public String toJson( SimpleTimeCarrier pTimeCarrier ) {
        try {
          return zMapper.writeValueAsString( pTimeCarrier );
        }
        catch ( JsonProcessingException e ) {
          throw new RuntimeException( e );
        }
      }

      @Override
      public SimpleTimeCarrier fromJson( String pJson ) {
        try {
          return zMapper.readValue( pJson, SimpleTimeCarrier.class );
        }
        catch ( IOException e ) {
          throw new RuntimeException( e );
        }
      }
    } );
  }

  @Test
  public void test_JsonRTviaGson() {
    Gson zMapper = new GsonBuilder().serializeNulls().create();
    checkWith( new BeanJsonConverter<SimpleTimeCarrier>( zMapper.getClass() ) {
      @Override
      public String toJson( SimpleTimeCarrier pTimeCarrier ) {
        return zMapper.toJson( pTimeCarrier );
      }

      @Override
      public SimpleTimeCarrier fromJson( String pJson ) {
        return zMapper.fromJson( pJson, SimpleTimeCarrier.class );
      }
    } );
  }

  private void checkWith( BeanJsonConverter<SimpleTimeCarrier> pConverter ) {
    checkRTwith( pConverter, null );
    checkRTwith( pConverter, "2011-01-16T17:18:19.200Z" );
    checkRTwith( pConverter, "1000-12-31T23:59:59.999Z" );
    checkRTwith( pConverter, "-1000-12-31T23:59:59.999Z" );
    checkRTwith( pConverter, "2018-09-01T00:00:00.000Z" );
  }

  private void checkRTwith( BeanJsonConverter<SimpleTimeCarrier> pConverter, String pISOform ) {
    ZuluDateTime zDateTimeOriginal = null;
    String zWrappedISOform = "null";
    if ( pISOform != null ) {
      zDateTimeOriginal = ZuluDateTime.parse( pISOform );
      zWrappedISOform = "\"" + pISOform + "\"";
    }

    String zSTCasJson = pConverter.toJson( new SimpleTimeCarrier().withDT( zDateTimeOriginal ) );
    Assert.assertEquals( pConverter.getActualConverterName() + ".{toJson}(" + zWrappedISOform + "')",
                         "{\"dt\":" + zWrappedISOform + "}",
                         zSTCasJson );

    SimpleTimeCarrier zRTedSTC = pConverter.fromJson( zSTCasJson );
    Assert.assertEquals( pConverter.getActualConverterName() + ".{fromJson}('" + zSTCasJson + "')",
                         zDateTimeOriginal,
                         zRTedSTC.getDt() );
  }
}