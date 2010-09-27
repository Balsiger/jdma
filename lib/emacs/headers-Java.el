;;
;; headers for Java files
;;

(defun my-insert-Java/l-file-header ()
  (interactive)
  (my-insert-Java-file-header)
)

(defun my-insert-Java-file-header ()
  "Inserts proper headers in a Java file."
  (interactive)
  (beginning-of-buffer)
  (insert "/******************************************************************************\n"
          " * Copyright (c) 2002-2007 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler\n"
          " * All rights reserved\n"
          " *\n"
          " * This file is part of Dungeon Master Assistant.\n"
          " *\n"
          " * Dungeon Master Assistant is free software; you can redistribute it and/or\n"
          " * modify it under the terms of the GNU General Public License as published by\n"
          " * the Free Software Foundation; either version 2 of the License, or\n"
          " * (at your option) any later version.\n"
          " *\n"
          " * Dungeon Master Assistant is distributed in the hope that it will be useful,\n"
          " * but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
          " * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"
          " * GNU General Public License for more details.\n"
          " *\n"
          " * You should have received a copy of the GNU General Public License\n"
          " * along with Dungeon Master Assistant; if not, write to the Free Software\n"
          " * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA\n"
          " *****************************************************************************/\n"
          "\n")
  (my-insert-line "//" "imports" "right" "-")
  (insert "\nimport javax.annotation.Nonnull;\n")
  (insert "import javax.annotation.Nullable;\n")
  (insert "\n")
  (my-insert-line "//" "" "right" ".")
  (insert "\n")
  (my-insert-line "//" "header" "right" "-")
  (insert "\n"
          "/**\n"
          " * \n"
          " * \n"
          " * \n"
          " * @file          " (file-name-nondirectory buffer-file-name) "\n"
          " * \n"
          " * @author        " user-full-name "\n"
          " *\n"
          " */\n"
          "\n"
  )
  (my-insert-line "//" "" "right" ".")
  (insert "\n")
  (my-insert-line "//" "" "right" "_")
  (insert "\n")
  (insert "public class "
          (file-name-sans-extension (file-name-nondirectory
                                     buffer-file-name))
          "\n"
          "{\n"
          )
  (my-insert-line "  //" "constructor(s)" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "variables" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "accessors" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "manipulators" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "other member functions" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "test" "right" "-")
  (insert "\n")
  (insert "  /** The test.\n")
  (insert "   *\n")
  (insert "   * @hidden\n")
  (insert "   *\n")
  (insert "   */\n")
  (insert "  public static class Test extends net.ixitxachitls.util.test.TestCase\n")
  (insert "  {\n")
  (insert "  }\n")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "\n")
  (my-insert-line "  //" "main/debugging" "right" "-")
  (insert "\n")
  (my-insert-line "  //" "" "right" ".")
  (insert "}\n")
)

(defun my-insert-Java/l-func-header ()
  (interactive)
  (my-insert-Java-func-header)
)

(defun my-insert-Java-func-header ()
  "Insert a Java function header"
  (interactive)
  (set 'my-col (current-column))
  (set 'my-temp (read-from-minibuffer "Function Name: "))
  (set 'my-temp-return (read-from-minibuffer "Return Type: "))
  (beginning-of-line)
  (my-insert-line (concat (make-string my-col ?\ ) "//") my-temp "center" "-")
  (insert "\n"
          (make-string my-col ?\ ) "/**\n"
          (make-string my-col ?\ ) " *\n"
          (make-string my-col ?\ ) " *\n"
          (make-string my-col ?\ ) " * @param       \n"
          (make-string my-col ?\ ) " *\n"
          (make-string my-col ?\ ) " * @return      \n"
          (make-string my-col ?\ ) " *\n"
          (make-string my-col ?\ ) " */\n"
          (make-string my-col ?\ ) "public " my-temp-return " " my-temp "()\n"
          (make-string my-col ?\ ) "{\n"
          (make-string my-col ?\ ) "\n"
          (make-string my-col ?\ ) "}\n"
          (make-string my-col ?\ ) "\n"
  )
  (my-insert-line (concat (make-string my-col ?\ ) "//") "" "" ".")
  (previous-line 4)
  (insert (concat (make-string my-col ?\ ) "  "))
)



