# temporal-additions-java

This repository purpose is to have a home to place (Java) solutions to recurring difficulties around Time (and Dates).

Most open source projects are an attempt to scratch an inch that has gone on for too long.
In this case the inintial impetus was a few related itches.  Itches:

1. Make code more testable that uses the static call (a testing anti-pattern) to System.currentTimeMillis() - See MillisecTimeSource.
2. Support the goal of easily human readable Time Stamps (without the ambiguity of missing time zones) in all intra-system communication.
3. Remove the effect of differing time zones for Time Stamps from your local dev machine (local time zone) and production servers (hopefully UTC).
4. Offer a solution to the issue raised here: &nbsp; https://github.com/swagger-api/swagger-codegen/issues/2138
  Specifically: For Restful JSON messaging use full ISO8601.
  The 4 proposed possible **java.time** Types:
   1. LocalDateTime - Issues:
      1. Sometimes renders as Json as array of numbers.
      2. partial ISO8601 - No time zone indicator.
      3. Since LocalDateTime is assumed to be in your local time zone when converting to the other types (e.g. Instant) if your local time zone is NOT UTC will adjust the values, highlighting Itch-3!
   2. ZonedDateTime - Issue:
       * Puts time zone 'Name' on output which is NOT ISO8601.
   3. OffsetDateTime - (all outputs are legitimate ISO8601 format) Issues:
      1. time zone offset does not always inclide the ':', which may mess with less lenient parsers.
      2. for offsets other than UTC (either 00:00 or 'z'), the String form does not sort correctly.
      3. if you want to use UTC always, you have to code for it diligently.
    4. Instant - (wonderful for messaging, perfect ISO8601 UTC format, with 'Z') Issue: 
       * due to the variable fractional second portion, the String form does not sort correctly.

   Also, re Itch-4, all the proposed possible Types, support static methods to create for "now()" (without taking a Clock), which encourages the problem referenced in Itch-1!

So, the proposed solution for Itches 2-4 is a new class called: ZuluDateTime
This new 'ZuluDateTime' class has the following properties:
1. derived from LocalDateTime and OffsetDateTime.
2. uses Instant internally for its ISO8601 UTC centricity format (always ends with 'Z').
3. limits the internal Instant to Millisec resolution (3 digits of fractional seconds)
4. forces the internal Instant when redering ISO8601 to always produce the 3 Millisec digits.
5. supports JSON serialization & deserialization for Jackson (via annotations) & Gson (annotation and Adapter).
