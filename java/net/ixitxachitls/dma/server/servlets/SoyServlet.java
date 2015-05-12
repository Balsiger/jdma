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

package net.ixitxachitls.dma.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.annotation.concurrent.Immutable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.base.Optional;
import com.google.template.soy.data.SoyData;

import org.easymock.EasyMock;

import net.ixitxachitls.dma.entries.BaseCharacter;
import net.ixitxachitls.dma.entries.BaseSkill;
import net.ixitxachitls.dma.entries.Level;
import net.ixitxachitls.dma.entries.Skill;
import net.ixitxachitls.dma.output.soy.SoyRenderer;
import net.ixitxachitls.dma.output.soy.SoyTemplate;
import net.ixitxachitls.dma.output.soy.SoyValue;
import net.ixitxachitls.dma.proto.Entries;
import net.ixitxachitls.dma.values.ArmorType;
import net.ixitxachitls.dma.values.Proficiency;
import net.ixitxachitls.dma.values.SizeModifier;
import net.ixitxachitls.dma.values.enums.Ability;
import net.ixitxachitls.dma.values.enums.Affects;
import net.ixitxachitls.dma.values.enums.Alignment;
import net.ixitxachitls.dma.values.enums.AttackMode;
import net.ixitxachitls.dma.values.enums.AttackStyle;
import net.ixitxachitls.dma.values.enums.CharacterState;
import net.ixitxachitls.dma.values.enums.Gender;
import net.ixitxachitls.dma.values.enums.Group;
import net.ixitxachitls.dma.values.enums.Immunity;
import net.ixitxachitls.dma.values.enums.Language;
import net.ixitxachitls.dma.values.enums.LanguageModifier;
import net.ixitxachitls.dma.values.enums.Maneuverability;
import net.ixitxachitls.dma.values.enums.MonsterSubtype;
import net.ixitxachitls.dma.values.enums.MonsterType;
import net.ixitxachitls.dma.values.enums.MovementMode;
import net.ixitxachitls.dma.values.enums.Organization;
import net.ixitxachitls.dma.values.enums.Save;
import net.ixitxachitls.dma.values.enums.Size;
import net.ixitxachitls.dma.values.enums.SkillModifier;
import net.ixitxachitls.dma.values.enums.SkillRestriction;
import net.ixitxachitls.dma.values.enums.SpellClass;
import net.ixitxachitls.dma.values.enums.SpellComponent;
import net.ixitxachitls.dma.values.enums.SpellDescriptor;
import net.ixitxachitls.dma.values.enums.SpellEffect;
import net.ixitxachitls.dma.values.enums.Subschool;
import net.ixitxachitls.util.Tracer;
import net.ixitxachitls.util.logging.Log;

/**
 * The base servlet for all soy rendered pages.
 *
 *
 * @file          SoyServlet.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

@Immutable
public class SoyServlet extends DMAServlet
{
  /**
    * Create the servlet.
    */
   public  SoyServlet()
   {
   }

  /** The id for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Get the name of the template to render the page.
   *
   * @param     inRequest the request for the page
   * @param     inData the soy data for rendering
   * @return    the name of the template
   */
  protected String getTemplateName(DMARequest inRequest,
                                   Map<String, SoyData> inData)
  {
    return "dma.page.empty";
  }

  /**
   * Checks whether the current page is public or requires some kind of
   * login.
   *
   * @param       inRequest the request for the page
   *
   * @return      true if public, false if login is required
   */
  public boolean isPublic(DMARequest inRequest)
  {
    return true;
  }

  @Override
  public String toString()
  {
    return "soy servlet with";
  }

  /**
   * Create the renderer for the given request.
   *
   * @param inRequest the request for the page
   * @return the renderer
   */
  protected SoyRenderer createRenderer(DMARequest inRequest)
  {
    Tracer tracer = new Tracer("creating renderer");
    SoyRenderer renderer = new SoyRenderer();
    tracer.done();
    // we have to collect injected data before other data to have it available
    // when collecting
    tracer = new Tracer("setting injected data");
    renderer.setInjected(collectInjectedData(inRequest, renderer));
    tracer.done();
    tracer = new Tracer("collecting data");
    Map<String, Object> data = collectData(inRequest, renderer);
    tracer.done();
    tracer = new Tracer("setting soy data");
    renderer.setData(data);
    tracer.done();

    return renderer;
  }

  /**
   * Render the page for the request.
   *
   * @param inRequest the request to render for
   * @param inWriter the writer to write to
   * @param inRenderer the soy renderer to render the page
   */
  protected void render(DMARequest inRequest, PrintWriter inWriter,
                        SoyRenderer inRenderer)
  {
    inWriter.println(inRenderer.render(getTemplateName(
        inRequest, inRenderer.getData())));
  }

  @Override
  protected Optional<? extends SpecialResult>
  handle(DMARequest inRequest, HttpServletResponse inResponse)
    throws ServletException, IOException
  {
    // Set the output header.
    inResponse.setContentType("text/html");
    inResponse.setCharacterEncoding("UTF-8");
    inResponse.setHeader("Cache-Control", "max-age=0");

    SoyRenderer renderer = createRenderer(inRequest);
    Tracer tracer = new Tracer("rendering soy template");
    try (PrintWriter writer = inResponse.getWriter())
    {
      writer.println(renderer.render("dma.page.intro"));
      render(inRequest, writer, renderer);
      writer.println(renderer.render("dma.page.extro"));
    }
    tracer.done();

    return Optional.absent();
  }

  @Override
  public void init()
  {
  }

  /**
   * Collect the data that is to be printed.
   *
   * @param    inRequest  the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  protected Map<String, Object> collectData(DMARequest inRequest,
                                            SoyRenderer inRenderer)
  {
    return SoyTemplate.map();
  }

  /**
   * Collect the injected data that is to be printed.
   *
   * @param    inRequest the request for the page
   * @param    inRenderer the renderer for rendering sub values
   *
   * @return   a map with key/value pairs for data (values can be primitives
   *           or maps or lists)
   */
  protected Map<String, Object> collectInjectedData
    (DMARequest inRequest, SoyRenderer inRenderer)
  {
    Tracer tracer = new Tracer("collecting soy injected data");
    Optional<BaseCharacter> user = inRequest.getUser();
    UserService userService = UserServiceFactory.getUserService();

    userService.createLoginURL(inRequest.getOriginalPath());
    Map<String, Object> map = SoyTemplate.map
      ("user", user.isPresent()
           ? new SoyValue(user.get().getKey().toString(), user.get()) : "",
       "isPublic", isPublic(inRequest),
       "originalPath", inRequest.getOriginalPath(),
       "loginURL", userService.createLoginURL(inRequest.getOriginalPath()),
       "logoutURL", userService.createLogoutURL(inRequest.getOriginalPath()),
       "registerScript",
       user.isPresent() && userService.isUserLoggedIn()
       ? "$().ready(function(){ register(); } );" : "",
       "userOverride", inRequest.hasUserOverride()
         ? inRequest.getRealUser().get().getName() : "",
       "isUser", user.isPresent(),
       "isAdmin", user.isPresent()
                  && user.get().hasAccess(Group.ADMIN),

       // classes with static access
       "Level", new SoyValue("Level", Level.class),
       "Size", new SoyValue("Size", Size.class),
       "SizeModifier", new SoyValue("SizeModifier", SizeModifier.class),
       "Skill", new SoyValue("Skill", Skill.class),
       "Gender", new SoyValue("Gender", Gender.class),
       "Alignment", new SoyValue("Alignment", Alignment.class),
       "Ability", new SoyValue("Ability", Ability.class),
       "Affects", new SoyValue("Affects", Affects.class),
       "WeaponProficiency",
       new SoyValue("WeaponProficiency", Proficiency.class),
       "ArmorProficiency",
       new SoyValue("ArmorProficiency", ArmorType.class),
       "SpellDescriptor",
       new SoyValue("SpellDescriptor", SpellDescriptor.class),
       "SpellEffect", new SoyValue("SpellEffect", SpellEffect.class),
       "SpellClass", new SoyValue("SpellClass", SpellClass.class),
       "Subschool", new SoyValue("Subschool", Subschool.class),
       "SpellComponent", new SoyValue("SpellComponent", SpellComponent.class),
       "MonsterType", new SoyValue("MonsterType", MonsterType.class),
       "MonsterSubtype", new SoyValue("MonsterSubtype", MonsterSubtype.class),
       "MovementMode", new SoyValue("MovementMode", MovementMode.class),
       "AttackMode", new SoyValue("AttackMode", AttackMode.class),
       "AttackStyle", new SoyValue("AttackStyle", AttackStyle.class),
       "CharacterState", new SoyValue("CharacterState", CharacterState.class),
       "Organization", new SoyValue("Organization", Organization.class),
       "Language", new SoyValue("Language", Language.class),
       "LanguageModifier",
       new SoyValue("LanguageModifier", LanguageModifier.class),
       "Maneuverability",
       new SoyValue("Maneuverability", Maneuverability.class),
       "Save", new SoyValue("Save", Save.class),
       "SkillModifier", new SoyValue("SkillModifier", SkillModifier.class),
       "SkillRestriction",
       new SoyValue("SkillRestriction", SkillRestriction.class),
       "Immunity", new SoyValue("Immunity", Immunity.class),
       "BaseSkill", new SoyValue("BaseSkill", BaseSkill.class));

    tracer.done();
    return map;
  }

  /**
   * Convert the given data into a map, using odd params as keys and even as
   * values.
   *
   * @param    inData the data to convert to a map
   *
   * @return   the converted map
   *
   */
  public static Map<String, Object> map(Object ... inData)
  {
    return SoyTemplate.map(inData);
  }

  //----------------------------------------------------------------------------

  /** The tests. */
  public static class Test extends net.ixitxachitls.server.ServerUtils.Test
  {
    /** The map test. */
    @org.junit.Test
    public void map()
    {
      assertEquals("empty", "{}", SoyServlet.map().toString());
      assertEquals("simple", "{second=b, third=c, first=a}",
                   SoyServlet.map("first", "a", "second", "b", "third", "c")
                   .toString());
    }

    /** The collectData test. */
    @org.junit.Test
    public void collectData()
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.replay(request);

      SoyServlet servlet = new SoyServlet();
      assertEquals("content", "{}",
                   servlet.collectData(request, new SoyRenderer())
                   .toString());

      EasyMock.verify(request);
    }

    /** The collectInjectedData test. */
    @org.junit.Test
    public void collectInjectedData()
    {
      DMARequest request = EasyMock.createMock(DMARequest.class);

      EasyMock.expect(request.getUser()).andStubReturn(
          Optional.<BaseCharacter>absent());
      EasyMock.expect(request.getOriginalPath()).andStubReturn("path");
      EasyMock.expect(request.hasUserOverride()).andStubReturn(false);
      EasyMock.replay(request);

      SoyServlet servlet = new SoyServlet();
      assertSomeContent("content",
                        servlet.collectInjectedData(request, new SoyRenderer()),
                        "isUser", "false",
                        "registerScript", "",
                        "userOverride", "",
                        "logoutURL", "/_ah/logout?continue=path",
                        "isAdmin", "false",
                        "originalPath", "path",
                        "isPublic", "true",
                        "user", "",
                        "loginURL", "/_ah/login?continue=path");

      EasyMock.verify(request);
    }
  }
}
