package com.altoros.temporal.iso8601.gson;

import java.io.IOException;

import com.altoros.temporal.iso8601.ZuluDateTime;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class GsonTypeAdapterZuluDateTime extends TypeAdapter<ZuluDateTime> {
  private static final char DOUBLE_QUOTE = '"';

  @Override
  public void write( JsonWriter pWriter, ZuluDateTime pDateTime )
          throws IOException {
    String zJson = (pDateTime == null) ? null : pDateTime.jsonValue();
    if ( zJson == null ) {
      pWriter.nullValue();
    } else {
      pWriter.jsonValue( DOUBLE_QUOTE + zJson + DOUBLE_QUOTE );
    }
  }

  @Override
  public ZuluDateTime read( JsonReader pReader )
          throws IOException {
    JsonToken zToken = pReader.peek();
    if ( zToken == JsonToken.NULL ) {
      pReader.nextNull();
      return null;
    }
    if ( zToken == JsonToken.STRING ) {
      String zValue = pReader.nextString();
      return ZuluDateTime.jsonCreator( zValue );
    }
    throw new IllegalStateException( "Expected a string but was " + zToken +
                                     " at" +
                                     //  " line " + line +     // Not Accessible!
                                     //  " column " + column + // Not Accessible!
                                     " path " + pReader.getPath() );
  }
}
