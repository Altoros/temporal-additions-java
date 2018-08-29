package com.altoros.temporal.jackson.iso8601;

import com.altoros.temporal.MillisecTimeSource;
import org.junit.Assert;
import org.junit.Test;

public class ZuluDateTimeTest {
  @Test
  public void test_JsonRT() {
    MillisecTimeSource zSource = MillisecTimeSource.SYSTEM;

    ZuluDateTime zInstance = ZuluDateTime.now( zSource );
    String zISO8601 = zInstance.toString();
    ZuluDateTime zRT = ZuluDateTime.parse( zISO8601 );
    Assert.assertEquals( zISO8601, zInstance, zRT );
    System.out.println( zISO8601 );

    checkRT( "2011-01-16T00:00:00Z", "2011-01-16T00:00:00.000Z" );
    checkRT( "2011-01-16T00:00:00.1Z", "2011-01-16T00:00:00.100Z" );
    checkRT( "2011-01-16T00:00:00.12Z", "2011-01-16T00:00:00.120Z" );
    checkRT( "2011-01-16T00:00:00.123Z", "2011-01-16T00:00:00.123Z" );
    checkRT( "2011-01-16T00:00:00.1234Z", "2011-01-16T00:00:00.123Z" );
  }

  private void checkRT( String pInISO8601, String pOutISO8601 ) {
    ZuluDateTime zInstance = ZuluDateTime.parse( pInISO8601 );
    String zActual = zInstance.toString();
    Assert.assertEquals( pOutISO8601, zActual );
    if ( !pInISO8601.equals( pOutISO8601 ) ) {
      ZuluDateTime zRT = ZuluDateTime.parse( zActual );
      Assert.assertEquals( "parsing: " + pInISO8601 + " != " + pOutISO8601, zRT, zInstance );
    }
  }
}