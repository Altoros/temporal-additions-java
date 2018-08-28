# temporal-additions-java

This repository purpose is to have a home to place (Java) solutions to recuring difficulties around Time (and Dates).

Most open source projects are an attempt to scratch an inch that has gone on for too long.
In this case the inintial impetis was a few related itches.  Specifically:

1) Make code more testable that uses the static call (a testing anti-pattern) to System.currentTimeMillis() - See MillisecTimeSource.
2) Support the goal of easily human readable Time Stamps (without the ambiguity of missing time zones) in all intra-system communication.
3) Remove the differing time zones for Time Stamps from your local dev machine (local time zone) and production servers (hopefully UTC).
4) Offer another solution to the issue raised here: https://github.com/swagger-api/swagger-codegen/issues/2138
    Specific: For Restful JSON messaging use full ISO8601, So issues with the following:
      LocalDateTime - Either produces an array of numbers OR partial ISO8601 - No time zone.
      ZonedDateTime - Puts time zone 'Name' on output which is NOT ISO8601.
      OffsetDateTime - has the following issues: 
                      a) time zone offset does not always inclide the ':', which may mess with less lenient parsers.
                      b) String form does not sort correctly - any offset other that UTC (either 00:00 or 'z').
                      c) if you want to use UTC always, you have to code for it diligently.
      Instant - Is wonderful for messaging, perfect ISO8601 UTC (with 'Z'), however has the following issue: 
                      String form does not sort correctly - due to the variable fractional portion.

Also, re #4, all the proposed solutions support static methods to create for "now()", which encourages the problem referenced in #1!

So, the proposed solution for #2 thru #4 is a new class derived from LocalDateTime and OffsetDateTime, that uses Instant, called ZuluDateTime which supports JSON (Jackson annotations) which is ISO8601 with 3 digits (always) of fractional seconds and UTC ('Z').


      
      
