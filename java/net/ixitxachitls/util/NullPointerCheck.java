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

//------------------------------------------------------------------ imports

package net.ixitxachitls.util;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

//..........................................................................

//------------------------------------------------------------------- header

/**
 * This is a special check for checkstyle to check if a method uses one of it's
 * arguments without checking it for null (and throwing an
 * IllegalArgumentException).
 *
 * @file          NullPointerCheck.java
 * @author        balsiger@ixitxachitls.net (Peter Balsiger)
 */

//..........................................................................

//__________________________________________________________________________

public class NullPointerCheck extends Check
{

  //--------------------------------------------------------- constructor(s)
  //........................................................................

  //-------------------------------------------------------------- variables
  //........................................................................

  //-------------------------------------------------------------- accessors

  //--------------------------- getDefaultTokens ---------------------------

  /**
    * Get the tokens that should be considered for this check (METHOD_DEF).
    *
    * @return      an array with all the tokens to consider
    */
  @Override
  public int[] getDefaultTokens()
  {
    return new int [] { TokenTypes.METHOD_DEF, };
  }

  //........................................................................

  //........................................................................

  //----------------------------------------------------------- manipulators
  //........................................................................

  //------------------------------------------------- other member functions

  //------------------------------ visitToken ------------------------------

  /**
    * Visit the token from the walker to make the checks.
    *
    * @param       inAST the node in the tree to check
    */
  @Override
  public void visitToken(DetailAST inAST)
  {
    if(inAST == null)
      throw new IllegalArgumentException("must have a node here");

    // check only public methods
    if(inAST.findFirstToken(TokenTypes.MODIFIERS) != null
       && inAST.findFirstToken(TokenTypes.MODIFIERS)
       .findFirstToken(TokenTypes.LITERAL_PUBLIC) != null)
    {
      // extract method name
      String method = inAST.findFirstToken(TokenTypes.IDENT).getText();

      // find parameters
      DetailAST parameters = inAST.findFirstToken(TokenTypes.PARAMETERS);

      // determine all the parameters used
      for(DetailAST param =
            parameters.findFirstToken(TokenTypes.PARAMETER_DEF);
          param != null;
          param = param.getNextSibling())
      {
        // ignore non parameters
        if(param.getType() != TokenTypes.PARAMETER_DEF)
          continue;

        // ignore parameters that are @Nonnull
        if(hasAnnotation(param, "Nonnull"))
          return;

        // ignore parameters that are in a @ParametersAreNonnullByDefault and
        // don't have @Nullable
        if(!hasAnnotation(param, "Nullable")
           && parentHasAnnotation(param, "ParametersAreNonnullByDefault"))
          return;


        String parameter = param.findFirstToken(TokenTypes.IDENT).getText();

        // check if we have a type that needs to be considered
        if(param.findFirstToken(TokenTypes.TYPE) != null
           && param.findFirstToken(TokenTypes.TYPE)
           .findFirstToken(TokenTypes.IDENT) != null)
        {
          // see if this parameter is already checked for null
          int check = findCheck(parameter, inAST);

          // see if we find a method call for this parameter
          int call = checkMethods(parameter, inAST);

          if(call > 0 && (call < check || check == 0))
          {
            log(inAST.getLineNo(), "Method '" + method
                + "()' does not check '" + parameter
                + "' for null value but uses it on line " + call);
          }
        }
      }
    }
  }

  //........................................................................
  //----------------------------- checkMethods -----------------------------

  /**
    * Check all the method call of the given node if they are called in an
    * object variable with the given name.
    * This actually determines if a method in the given object is called.
    *
    * @param       inParameter the name of the object to check for calls
    * @param       inNode      the node to look in (including all children)
    *
    * @return      the line number of the line where a method call was found
    *              or 0 if none was found
    *
    */
  private int checkMethods(String inParameter, DetailAST inNode)
  {
    // already done?
    if(inNode == null)
      return 0;

    // do we have a method call?
    if(inNode.getType() == TokenTypes.METHOD_CALL)
    {
      DetailAST dot = inNode.findFirstToken(TokenTypes.DOT);

      // ignore non object calls
      if(dot != null)
      {
        // check if the name matches
        if(dot.getFirstChild().getType() == TokenTypes.IDENT
           && inParameter.equals(dot.getFirstChild().getText()))
          return dot.getLineNo();
      }
    }

    // do we have a variable use?
    if(inNode.getType() == TokenTypes.DOT)
    {
      DetailAST first = inNode.findFirstToken(TokenTypes.IDENT);

      // check if the name matches
      if(first != null && inParameter.equals(first.getText()))
        return first.getLineNo();
    }

    // check if we have other method calls
    for(DetailAST node = inNode.getFirstChild();
        node != null;
        node = node.getNextSibling())
    {
      int line = checkMethods(inParameter, node);

      if(line > 0)
        return line;
    }

    // nothing found
    return 0;
  }

  //........................................................................
  //------------------------------ findCheck -------------------------------

  /**
    * Determine if in the given node there is a check for null of the named
    * object. It is only checked if there is a null test, not what action is
    * taken in such a case.
    *
    * @param       inParameter the parameter to check for
    * @param       inNode      the node to look in
    *
    * @return      the line number where the check was found or 0 if none was
    *              found
    */
  private int findCheck(String inParameter, DetailAST inNode)
  {
    // at the end?
    if(inNode == null)
      return 0;

    // do we have an if?
    if(inNode.getType() == TokenTypes.LITERAL_IF)
    {
      int line = findEquals(inParameter, inNode);

      // found something?
      if(line > 0)
        return line;
    }

    // check if we have other checks
    for(DetailAST node = inNode.getFirstChild();
        node != null;
        node = node.getNextSibling())
    {
      int line = findCheck(inParameter, node);

      if(line > 0)
        return line;
    }

    // nothing found
    return 0;
  }

  //........................................................................
  //------------------------------ findEquals ------------------------------

  /**
    * Find equal (and not equals) operators inside an if.
    *
    * @param       inParameter the parameter to look for
    * @param       inNode      the node to look in
    *
    * @return      the line number where the equals operator with the name was
    *              found, or 0 if none was found
    *
    */
  private int findEquals(String inParameter, DetailAST inNode)
  {
    if(inNode == null)
      return 0;

    if(inNode.getType() == TokenTypes.EQUAL
       || inNode.getType() == TokenTypes.NOT_EQUAL)
    {
      // check of parameter == null
      if(inNode.getFirstChild().getType() == TokenTypes.IDENT
         && inParameter.equals(inNode.getFirstChild().getText())
         && inNode.getFirstChild().getNextSibling().getType()
         == TokenTypes.LITERAL_NULL)
        return inNode.getLineNo();
    }

    // check if we have other inNodeerators
    for(DetailAST node = inNode.getFirstChild();
        node != null;
        node = node.getNextSibling())
    {
      int line = findEquals(inParameter, node);

      if(line > 0)
        return line;
    }

    // nothing found
    return 0;
  }

  //........................................................................
  //------------------------- parentHasAnnotation --------------------------

  /**
   * Check if any of the parents has the given annotation.
   *
   * @param   inNode        the node the start checking from
   * @param   inAnnotation  the name of the anntoation to look for
   *
   * @return  true if the annotation is found in any parent, false if not
   */
  private boolean parentHasAnnotation(DetailAST inNode, String inAnnotation)
  {
    for(DetailAST parent = inNode.getParent(); parent != null;
        parent = parent.getParent())
      if(hasAnnotation(parent, inAnnotation))
        return true;

    return false;
  }

  //........................................................................
  //---------------------------- hasAnnotation -----------------------------

  /**
   * Check if the given node has he name annotation.
   *
   * @param inNode        the node the check in
   * @param inAnnotation  the name of the annotation to look for
   *
   * @return true if the annotation is found, false if not
   */
  private boolean hasAnnotation(DetailAST inNode, String inAnnotation)
  {
    if(inNode.branchContains(TokenTypes.ANNOTATION))
      for(DetailAST child = inNode.getFirstChild(); child != null;
              child = child.getNextSibling())
            if(child.getType() == TokenTypes.MODIFIERS)
              for(DetailAST modifier = child.getFirstChild(); modifier != null;
                  modifier = modifier.getNextSibling())
                if(modifier.getType() == TokenTypes.ANNOTATION)
                {
                  DetailAST first = modifier.findFirstToken(TokenTypes.IDENT);
                  if(first != null && inAnnotation.equals(first.getText()))
                    return true;
                }

    return false;
  }

  //........................................................................

  //........................................................................

  //------------------------------------------------------------------- test

  // no tests here...

  //........................................................................
}
