/******************************************************************************
 * Copyright (c) 2002-2012 Peter 'Merlin' Balsiger and Fred 'Mythos' Dobler
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

//------------------------------------------------------------------ imports

package net.ixitxachitls.dma.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.tofu.SoyTofu;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.output.soy.SoyEntry;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyTemplate;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * The base servlet for all soy rendered pages.
 *
 *
 * @file          SoyServlet.java
 *
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 *
 */

//..........................................................................

//__________________________________________________________________________

public abstract class SoyServlet extends DMAServlet
{
  //--------------------------------------------------------- constructor(s)

  //----------------------------- SoyServlet -------------------------------

   /**
    * Create the servlet.
    *
    */
   public  SoyServlet()
   {
     // nothing to do
   }

  //........................................................................

  //........................................................................

  //-------------------------------------------------------------- variables

  /** The template to render a page. */
  protected static final SoyTemplate s_template =
     new SoyTemplate("page", "errors", "about", "main", "navigation", "entry",
                     "commands", "value",
                     "basecharacter", "character",
                     "baseproduct", "product",
                     "baseitem",
                     "basecampaign");

  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getTemplateName ----------------------------

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   *
   * @return    the name of the template
   *
   */
  protected @Nonnull String getTemplateName(@Nonnull DMARequest inRequest)
  {
    if(inRequest.isBodyOnly())
      return "dma.page.bodyOnly";

    return "dma.page.full";
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators

  //........................................................................

  //------------------------------------------------- other member functions

  //-------------------------------- handle --------------------------------

  /**
   * Handle the request.
   *
   * @param       inRequest  the original request
   * @param       inResponse the original response
   *
   * @return      a special result if something went wrong
   *
   * @throws      ServletException general error when processing the page
   * @throws      IOException      writing to the page failed
   *
   */
  @Override
  protected @Nullable SpecialResult handle
    (@Nonnull DMARequest inRequest,
     @Nonnull HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    if(isDev())
    {
      s_template.recompile();
      SoyRenderer.recompile();
    }

    // Set the output header.
    inResponse.setHeader("Content-Type", "text/html");
    inResponse.setHeader("Cache-Control", "max-age=0");

    SoyRenderer renderer = new SoyRenderer(s_template);
    // we have to collect injected data before other data to have it available
    // when collecting
    renderer.setInjected(collectInjectedData(inRequest, renderer));
    renderer.setData(collectData(inRequest, renderer));

    PrintWriter print = new PrintWriter(inResponse.getOutputStream());
    print.println(renderer.render(getTemplateName(inRequest)));
    print.close();

    return null;
  }

  //........................................................................

  //-------------------------------- render --------------------------------

  /**
   * Render a template with the given data.
   *
   * @param       inName      the name of the template to render
   * @param       inData      the data to use for rendering
   * @param       inInjected  the injected data for rendering
   * @parm        inDelegates the active delegates
   *
   * @return      the rendered template as string
   *
   */
  // public @Nonnull String render(@Nonnull String inName,
  //                               @Nullable Map<String, Object> inData,
  //                               @Nullable Map<String, Object> inInjected,
  //                               @Nullable Set<String> inDelegates)
  // {
  //   return s_template.render(inName, inData, inInjected, inDelegates);
  // }

  //........................................................................
  //-------------------------------- render --------------------------------

  /**
   * Render a template with the given data.
   *
   * @param       inName     the name of the template to render
   * @param       inData     the data to use for rendering
   * @param       inInjected the injected data for rendering
   *
   * @return      the rendered template as string
   *
   */
  // public @Nonnull String render(@Nonnull String inName,
  //                               @Nullable Map<String, Object> inData,
  //                               @Nullable Map<String, Object> inInjected)
  // {
  //   return render(inName, inData, inInjected, null);
  // }

  //........................................................................
  //-------------------------------- render --------------------------------

  /**
   * Render a template without data.
   *
   * @param       inName     the name of the template to render
   *
   * @return      the rendered template as string
   *
   */
  // public @Nonnull String render(@Nonnull String inName)
  // {
  //   return render(inName, null, null, null);
  // }

  //........................................................................

  //----------------------------- collectData ------------------------------

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest the request for the page
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  protected @Nonnull Map<String, Object> collectData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer renderer)
  {
    return s_template.map("oldcontent", "");
  }

  //........................................................................
  //------------------------- collectInjectedData --------------------------

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest the request for the page
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   *
   */
  protected @Nonnull Map<String, Object> collectInjectedData
    (@Nonnull DMARequest inRequest, @Nonnull SoyRenderer renderer)
  {
    BaseCharacter user = inRequest.getUser();
    UserService userService = UserServiceFactory.getUserService();

    return s_template.map
      ("user", user == null ? "" : new SoyEntry(user, renderer),
       "loginURL", userService.createLoginURL(inRequest.getOriginalPath()),
       "logoutURL", userService.createLogoutURL(inRequest.getOriginalPath()),
       "registerScript",
       user == null && userService.isUserLoggedIn()
       ? "$().ready(function(){ register(); } );" : "",
       "userOverride",
       inRequest.hasUserOverride() ? inRequest.getRealUser().getName() : "",
       "isUser", user != null,
       "isAdmin", user != null && user.hasAccess(BaseCharacter.Group.ADMIN));
  }

  //........................................................................
  //--------------------------------- map ----------------------------------

  /**
   * Convert the given data into a map, using odd params as keys and even as
   * values.
   *
   * @param    inData the data to convert to a map
   *
   * @return   the converted map
   *
   */
  public @Nonnull Map<String, Object> map(Object ... inData)
  {
    return s_template.map(inData);
  }

  //........................................................................
  //........................................................................

  //------------------------------------------------------------------- test

  /** The tests. */
  public static class Test extends net.ixitxachitls.util.test.TestCase
  {
  }

  //........................................................................

  //--------------------------------------------------------- main/debugging

  //........................................................................
}
