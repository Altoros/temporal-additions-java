package com.altoros.temporal;

import java.time.Clock;
import java.util.Optional;

import org.litesoft.annotations.NotNull;
import org.litesoft.annotations.Nullable;

/**
 * This interface, and its two default implementations, are designed to support the creation of more testable
 * code by making it easier to replace the static call (a testing anti-pattern) to <code>System.currentTimeMillis()</code>.
 */
@FunctionalInterface
public interface MillisecTimeSource {
  /**
   * Get the current Time in Millisecs.
   * <p>
   * Note: the method's signature is the same as <code>System.currentTimeMillis()</code>
   * specifically so that the static call (a testing anti-pattern) to System is a little easier to replace.
   *
   * @return current Time in Millisecs
   */
  long currentTimeMillis();

  /**
   * Implementation of <code>MillisecTimeSource</code> using <code>System.currentTimeMillis()</code>.
   * <p>
   * Note: this Implementation is returned by the <code>deNull</code> methods when the parameter is <code>null</code> or doesn't exist.
   */
  MillisecTimeSource SYSTEM = System::currentTimeMillis;

  /**
   * Map a <code>java.time.Clock</code> to a <code>MillisecTimeSource</code>.
   *
   * @param pClock if null the <code>Clock.systemUTC()</code> version will be used
   *
   * @return !null <code>MillisecTimeSource</code>
   */
  @NotNull
  static MillisecTimeSource from( @Nullable Clock pClock ) {
    if ( pClock == null ) {
      pClock = Clock.systemUTC();
    }
    return pClock::millis;
  }

  /**
   * Convenience method to return the <code>SYSTEM</code> instance IF the passed in instance is null.
   *
   * @param pMillisecTimeSource if null the <code>SYSTEM</code> instance will be used
   *
   * @return !null <code>MillisecTimeSource</code>
   */
  @SuppressWarnings("unused")
  @NotNull
  static MillisecTimeSource deNull( @Nullable MillisecTimeSource pMillisecTimeSource ) {
    return (pMillisecTimeSource != null) ? pMillisecTimeSource : SYSTEM;
  }

  /**
   * Convenience method to return the <code>SYSTEM</code> instance IF the passed in instance is null or not present.
   *
   * @param pMillisecTimeSource if null or not present the <code>SYSTEM</code> instance will be used
   *
   * @return !null <code>MillisecTimeSource</code>
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @NotNull
  static MillisecTimeSource deNull( @Nullable Optional<MillisecTimeSource> pMillisecTimeSource ) {
    return ((pMillisecTimeSource != null) && pMillisecTimeSource.isPresent()) // Left to Right
           ? pMillisecTimeSource.get() : SYSTEM;
  }
}
