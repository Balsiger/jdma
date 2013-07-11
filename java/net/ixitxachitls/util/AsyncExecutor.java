/******************************************************************************
 * Copyright (c) 2002-2011 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler
 * All rights reserved
 *
 * This file is part of Dungeon Master Assistant.
 *
 * Dungeon Master Assistant is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Master Assistant is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dungeon Master Assistant; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/
// $codepro.audit.disable abstractSpecialization

//------------------------------------------------------------------ imports

package net.ixitxachitls.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import net.ixitxachitls.util.logging.Log;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * Asynchronously execute things from objects given into a queue.
 *
 * @file          AsyncExecutor.java
 *
 * @author        balsiger@ixixachitls.net (Peter Balsiger)
 * @param         <T> the type of objects to exchange with the executor
 */

//..........................................................................

//__________________________________________________________________________

@ThreadSafe
@ParametersAreNonnullByDefault
public abstract class AsyncExecutor<T> extends Thread
{
  //--------------------------------------------------------- constructor(s)

  /**
   * Create the asynchronous executor.
   *
   * @param inDelayFull the time between working through all current request
   *                    and starting to check for new, in mili seconds
   * @param inDelayEach the minimal time between each execution
   * @param inSize      the number of elements to queue before a provider is
   *                    blocked
   *
   */
  protected AsyncExecutor(long inDelayFull, long inDelayEach, int inSize)
  {
    m_fullDelay = inDelayFull;
    m_eachDelay = inDelayEach;

    m_queue = new ArrayBlockingQueue<T>(inSize);
  }

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The polling time to check the queue, in mili seconds. */
  private long m_fullDelay;

  /** The time between each execution. */
  private long m_eachDelay;

  /** Flag if processing is done or not. */
  protected boolean m_done = false;

  /** The queue to store the stuff to be tracked. */
  private final BlockingQueue<T> m_queue;

  //........................................................................

  //-------------------------------------------------------------- accessors

  //-------------------------------- isDone --------------------------------

  /**
   * Check if the executor is finished.
   *
   * @return      true if done, false if not
   *
   */
  public synchronized boolean isDone()
  {
    return m_done && m_queue.size() == 0;
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //-------------------------------- offer ---------------------------------

  /**
   * Offer the given object for asyncronous execution.
   *
   * @param       inValue   the value to offer for execution
   * @param       inTimeout the number of mili seconds to wait until offering
   *                        fails
   *
   * @return      true if accepted, false if not
   *
   */
  public boolean offer(T inValue, long inTimeout)
  {
    if(isDone())
      return false;

    try
    {
      boolean result = m_queue.offer(inValue, inTimeout, TimeUnit.MILLISECONDS);
      return result;
    }
    catch(InterruptedException e)
    {
      return false;
    }
  }

  //........................................................................

  //--------------------------------- done ---------------------------------

  /**
   * Mark the executor as being done and termiante it at next opportune moment.
   *
   */
  public synchronized void done()
  {
    m_done = true;
  }

  //........................................................................
  //------------------------------- execute --------------------------------

  /**
   * Execute the action for an object presented.
   *
   * @param       inValue the value to execute with
   *
   */
  public abstract void execute(T inValue);

  //........................................................................

  //........................................................................

  //------------------------------------------------- other member functions

  //--------------------------------- run ----------------------------------

  /**
   * Process all queued messages or wait for more.
   *
   */
  @Override
  public void run()
  {
    try
    {
      while(!isDone())
      {
        T value = m_queue.poll(m_fullDelay, TimeUnit.MILLISECONDS);

        if(value != null)
          execute(value);

        if(!isDone())
          sleep(m_eachDelay);
      }
    }
    catch(InterruptedException e)
    {
      Log.warning("Async executor interrupted, shutting down");
    }
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  /** The test. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
    //----- execution ------------------------------------------------------

    /** Test simple execution.
     *
     * @throws Exception anything that goes wrong
     *
     */
    @org.junit.Test
    public void execution() throws Exception
    {
      final java.util.List<String> results = new java.util.ArrayList<String>();

      AsyncExecutor<String> executor = new AsyncExecutor<String>(0, 0, 4)
        {
          @Override
          public void execute(String inValue)
          {
            while(!m_done)
            {
              // nothing to do
            }

            results.add(inValue);
         }
        };

      executor.start();

      executor.offer("first", 0);
      executor.offer("second", 0);
      executor.offer("third", 0);
      executor.offer("fourth", 0);
      executor.offer("fifth", 0);
      // this is only true if execution of the first element already started
      boolean additional = executor.offer("sixth", 0);

      executor.done();
      executor.join(5 * 1000);

      assertEquals("result", "first", results.get(0));
      assertEquals("result", "second", results.get(1));
      assertEquals("result", "third", results.get(2));
      assertEquals("result", "fourth", results.get(3));
      if(additional)
      {
        assertEquals("result", "fifth", results.get(4));
        assertEquals("result size", 5, results.size());
      }
      else
        assertEquals("result size", 4, results.size());
    }

    //......................................................................
    //----- capacity -------------------------------------------------------

    /** Test the capacity of the queue.
     *
      * @throws Exception anything that goes wrong
     *
     */
    @org.junit.Test
    public void capacity() throws Exception
    {
      final java.util.List<String> results = new java.util.ArrayList<String>();

      AsyncExecutor<String> executor = new AsyncExecutor<String>(0, 0, 2)
        {
          @Override
          public void execute(String inValue)
          {
            while(!m_done)
            {
              // notning to do
            }

            results.add(inValue);
          }
        };


      executor.start();

      assertTrue(executor.offer("first", 0));
      assertTrue(executor.offer("second", 0));
      // this is only true if execution of the first element already started
      boolean additional = executor.offer("third", 0);
      assertFalse(executor.offer("fourth", 0));

      executor.done();
      executor.join(5 * 1000);

      assertEquals("result", "first", results.get(0));
      assertEquals("result", "second", results.get(1));
      if(additional)
      {
        assertEquals("result", "third", results.get(2));
        assertEquals("result", 3, results.size());
      }
      else
        assertEquals("result", 2, results.size());
    }

    //......................................................................
  }

  //........................................................................
}
