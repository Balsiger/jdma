;;
;; macros for printing headers
;;

;; set some functions as outloading from the corresponding files
(autoload 'my-insert-C++-file-header "headers-C++" 
           "Insert the C++ file header" t t)
(autoload 'my-insert-C++-func-header "headers-C++" 
           "Insert the C++ function header" t t)
(autoload 'my-insert-Java/l-file-header "headers-Java" 
           "Insert the Java file header" t t)
(autoload 'my-insert-Java/l-func-header "headers-Java" 
           "Insert the Java function header" t t)
(autoload 'my-insert-Perl-file-header "headers-perl" 
          "Insert the perl file header" t t)
(autoload 'my-insert-Perl-func-header "headers-perl" 
          "Insert the perl function header" t t)
(autoload 'my-insert-HTML-file-header "headers-html" 
          "Insert the html file header" t t)
(autoload 'my-insert-HTML-func-header "headers-html" 
          "Insert the html function header" t t)

;; install the header function
(defun my-mode-header-install ()
  "Install the c++-mode headers"
  (local-set-key [f3] (intern (concat "my-insert-" mode-name 
                                      "-file-header")))
  (local-set-key [f4] (intern (concat "my-insert-" mode-name 
                                      "-func-header")))
  (fold-mode)
  ;;(flyspell-mode)
)

;; correct the comment starter for C++
;(defun my-c++-install ()
;  "Install corrections for C++"
;  (set 'comment-start "//")
;)

;; correct the comment starter for C++
;(defun my-java-install ()
;  "Install corrections for Java"
;  (set 'comment-start "//")
;)

;; install the mode hooks
(add-hook 'c++-mode-hook 'my-mode-header-install)
(add-hook 'c++-mode-hook 'my-c++-install)
(add-hook 'java-mode-hook 'my-mode-header-install)
(add-hook 'perl-mode-hook 'my-mode-header-install)
(add-hook 'html-mode-hook 'my-mode-header-install)
(add-hook 'text-mode-hook 'my-mode-header-install)

;; some additional help functions
(defun my-insert-line (indent caption position character)
  "Print a lined line"
  (insert indent)
  (if (string= caption "")
      (insert (make-string (- fill-column (string-width indent) 3)
                                          (string-to-char character)))
      (if (string= position "right")
          (insert (make-string (- fill-column (string-width indent) 
                                  (string-width caption)
                                  4)
                               (string-to-char character))
                  " " caption
          )
          (if (string= position "left")
              (insert (make-string 5 (string-to-char character)) " " caption 
                      " "
                      (make-string (- fill-column (string-width indent) 
                                      (string-width caption)
                                      10)
                                   (string-to-char character))
              )
              (insert (make-string (/ (- fill-column (string-width indent) 
                                         (string-width caption)
                                         5)
                                      2)
                                   (string-to-char character))
                      " " caption " "
                      (make-string (/ (- fill-column (string-width indent) 
                                         (string-width caption)
                                         4)
                                      2)
                                   (string-to-char character))
              )
          )
      )
  )
  (insert "\n")
)

(defun my-insert-fold (position &optional text)
  "Insert an aligned fold"
  (set 'my-temp (current-column))
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  (if text
                      text
                      (read-from-minibuffer "Fold Name: ")
                  )
                  position "-")
  (insert "\n\n\n")
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (previous-line 3)
  (insert (make-string my-temp ?\ ))
;; a hack to ensure that fold will notice the new fold
  (fold-mode)
  (fold-mode)
)

(defun my-insert-right-fold ()
  "Insert a left aligned fold"
  (interactive)
  (my-insert-fold "right")
)

(defun my-insert-left-fold ()
  "Insert a left aligned fold"
  (interactive)
  (my-insert-fold "left")
)

(defun my-insert-center-fold ()
  "Insert a left aligned fold"
  (interactive)
  (my-insert-fold "center")
)

(defun my-insert-DMA (&optional text)
  "Insert an empty DMA template of the appropriate type"
  (interactive)
  (set 'my-type (completing-read "Type of entry: " 
                                 '(("product" 1) ("base-product" 2)
                                   ("base-item" 3) ("adventure" 4) 
                                   ("image" 5) ("base-spell" 6) 
                                   ("base-effect" 7) ("base-monster" 8) 
                                   ("base-quality" 9) ("base-encounter" 10)
                                   ("encounter" 11))
                                 nil t))
  (funcall (intern (concat "my-insert-DMA-" my-type)) text)
)

(defun my-insert-copyright ()
  "insert the copyright notice"
  (interactive)
  (beginning-of-buffer)
  (insert "/******************************************************************************\n"
          " * Copyright (c) 2002,2003 Peter 'Merlin' Balsiger and Fredy 'Mythos' Dobler\n"
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
)

(defun my-insert-icon-image (&optional text)
  "Insert an icon image for the web page"
  (interactive)
  (set 'my-category (read-from-minibuffer "Category: "))
  (set 'my-name (read-from-minibuffer "Name: "))
  (set 'my-notes (read-from-minibuffer "Notes: "))
  (beginning-of-line)
  (insert "  <TABLE>\n"
          "    <TR>\n"
          "      <TD>\n"
          "        <IMG BORDER=0 SRC=\"../Archive/icons/" 
          my-category "/" my-name ".png\" \n"
          "             ALT=\"" my-name "\" \n"
          "             onMouseOver=\"iconHighlight(this);\" "
          "onMouseOut=\"iconNormal(this);\">\n"
          "      </TD>\n"
          "    </TR>\n"
          "    <TR><TD>" my-name "</TD></TR>  \n"
          "    <TR>\n"
          "      <TD CLASS=\"notes\">\n"
          "        " my-notes "\n"
          "      </TD>\n"
          "    </TR>\n"
          "  </TABLE>\n")
  )

(defun my-insert-DMA-base-product (&optional text)
  "Insert an empty DMA template of the base product type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ )
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "base product ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  title           \"\";\n"
          "  leader          \"\";\n"
          "  author          \"\", \"\";\n"
          "  editor          \"\", \"\";\n"
          "  cover           \"\", \"\";\n"
          "  cartography     \"\", \"\";\n"
          "  illustrations   \"\", \"\";\n"
          "  typography      \"\", \"\";\n"
          "  management      \"\", \"\";\n"
          "  synonyms        ;\n"
          "  date            ;\n"
          "  ISBN            ;\n"
          "  ISBN13          ;\n"
          "  pages           ;\n"
          "  producer        WTC;\n"
          "  system          D&D 3.5;\n"
          "  world           ;\n"
          "  audience        ;\n"
          "  type            ;\n"
          "  style           ;\n"
          "  layout          ;\n"
          "  volume          ;\n"
          "  series          ;\n"
          "  number          ;\n"
          "  price           $ .;\n"
          "  contents        ;\n"
          "  references      ;\n"
          "  requirements    ;\n"
          "  subtitle        \"\";\n"
          "  short description \"\";\n"
          "  description     \n"
          "\n"
          "  \"\".\n\n")
  (my-insert-line (concat (make-string my-temp ?\ )
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -31)
  (forward-char 17)
)

(defun my-insert-DMA-base-item (&optional text)
  "Insert an empty DMA template of the base item type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (insert "#----- ")
  (insert my-text)
  (insert "\n")
  (insert "\n"
          "base item ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  player name       \"\";\n"
          "  categories        ,;\n"
          "  synonyms          \"\", \"\";\n"
          "  value             1 gp;\n"
          "  weight            1 lbs;\n"
          "  size              medium-size;\n"
          "  probability       common;\n"
          "  substance           inch;\n"
          "  hardness          0;\n"
          "  hp                1;\n"
          "  appearances       very rare \"\",\n"
          "                    rare      \"\",\n"
          "                    uncommon  \"\",\n"
          "                    common    \"\",\n"
          "                    common    \"\",\n"
          "                    common    \"\",\n"
          "                    uncommon  \"\",\n"
          "                    rare      \"\",\n"
          "                    very rare \"\";\n"
          "  world             generic;\n"
          "  references        \"\" x;\n"
          "  short description \"\";\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (insert "#.....\n")
  (forward-line -24)
  (forward-char 16)
)

(defun my-insert-DMA-base-spell (&optional text)
  "Insert an empty DMA template of the base spell type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (insert "#----- ")
  (insert my-text)
  (insert "\n")
  (insert "\n"
          "base spell ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  categories        ,;\n"
          "  synonyms          \"\", \"\";\n"
          "  school            ;\n"
          "  casting time      ;\n"
          "  components        ;\n"
          "  range             ;\n"
          "  target            ;\n"
          "  duration          ;\n"
          "  daving throw      ;\n"
          "  spell resistance  ;\n"          
          "  references        \"WTC 17524\" 1;\n"
          "  short description \"\";\n"
          "  world             generic;\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (insert "#.....")
  (forward-line -7)
  (forward-char 16)
)

(defun my-insert-DMA-base-effect (&optional text)
  "Insert an empty DMA template of the base effect type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "base effect ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  synonyms          \"\", \"\";\n"
          "  type              ;\n"
          "  references        \"WTC 17524\" 1;\n"
          "  short description \"\";\n"
          "  world             generic;\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -7)
  (forward-char 16)
)

(defun my-insert-DMA-base-monster (&optional text)
  "Insert an empty DMA template of the base monster type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "base monster ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  synonyms          \"\", \"\";\n"
          "  size               ();\n"
          "  type               ();\n"
          "  hit dice          ;\n"
          "  speed             ;\n"
          "  natural armor     ;\n"
          "  base attack       ;\n"
          "  primary attacks   ;\n"
          "  special attacks   ;\n"
          "  special qualities ;\n"
          "  strength          ;\n"
          "  dexterity         ;\n"
          "  constitution      ;\n"
          "  intelligence      ;\n"
          "  wisdom            ;\n"
          "  charisma          ;\n"
          "  class skills      ;\n"
          "  feats             ;\n"
          "  environment       ;\n"
          "  organization      ;\n"
          "  challenge rating  ;\n"
          "  treasure          ;\n"
          "  alignment         ;\n"
          "  advancements      ;\n"
          "  level adjustment  ;\n"
          "  encounter         ;\n"
          "  combat            ;\n"
          "  languages         ;\n"
          "  tactics           ;\n"
          "  character         ;\n"
          "  reproduction      ;\n"
          "  short description \"\";\n"
          "  world             generic;\n"
          "  references        \"WTC 17755\" 1;\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -39)
  (forward-char 21)
)

(defun my-insert-DMA-base-quality (&optional text)
  "Insert an empty DMA template of the base quality type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (insert "#----- ")
  (insert my-text)
  (insert "\n")
  (insert "\n"
          "base quality ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  synonyms          \"\", \"\";\n"
          "  categories        action, attack, aura, communication, health, "
          "immunity, option, resistance, sense, vulnerability;\n"
          "  type              Ex/Su/Sp;\n"
          "  short description \"\";\n"
          "  world             generic;\n"
          "  references        \"WTC 17755\" 1;\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (insert "#.....\n")
  (forward-line -10)
  (forward-char 21)
)

(defun my-insert-DMA-base-encounter (&optional text)
  "Insert an empty DMA template of the base encounter type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "base encounter ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  synonyms          \"\", \"\";\n"
          "  adventure         ;\n"
          "  location          \"\";\n"
          "  skills            ;\n"
          "  short description \"\";\n"
          "  world             generic;\n"
          "  references        \"WTC 17755\" 1;\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -10)
  (forward-char 21)
)

(defun my-insert-DMA-encounter (&optional text)
  "Insert an empty DMA template of the encounter type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (insert "#----- ")
  (insert my-text)
  (insert "\n\n"
          "encounter ")
  (insert my-text)
  (insert " [] = \n"
          "\n"
          "  number            ;\n"
          "  distance          ft;\n"
          "  npcs              ;\n"
          "  monsters          ;\n"
          "  events            \"\";\n"
          "  rules             \"\";\n"
          "  spells            ;\n"
          "  EL                ;\n"
          "  treasure          ;\n"
          "  traps             \"\";\n"
          "  hazards           \"\";\n"
          "  obstacles         \"\";\n"
          "  secrets           \"\";\n"
          "  skills            ;\n"
          "  short description \"\";\n"
          "  description       \n"
          "\n"
          "  \"\".\n\n")
  (insert "#.....")
  (forward-line -10)
  (forward-char 21)
)

(defun my-insert-DMA-product (&optional text)
  "Insert an empty DMA template of the product type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "product ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  edition       1st;\n"
          "  printing      1st;\n"
          "  owner         Merlin;\n"
          "  status        available;\n"
          "  condition     good.\n\n")
  (my-insert-line (concat (make-string my-temp ?\ )
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -7)
  (forward-char 17)
)

(defun my-insert-DMA-adventure (&optional text)
  "Insert an empty DMA template of the adventure type"
  (interactive)
  (set 'my-temp (current-column))
  (set 'my-text (if text
                    text
                    (read-from-minibuffer "Identifier: ")
                )
  )
  (beginning-of-line)
  (my-insert-line (concat (make-string my-temp ?\ ) 
                          (string-trim '(?\ ) comment-start))
                  my-text "left" "-")
  (insert "\n"
          "adventure ")
  (insert my-text)
  (insert " = \n"
          "\n"
          "  title         \"\";\n"
          "  leader        \"\";\n"
          "  author        \"\", \"\", ...;\n"
          "  cartography   \"\", \"\", ...;\n"
          "  illustrations \"\", \"\", ...;\n"
          "  pages         ;\n"
          "  level          (-);\n"
          "  party          (-);\n"
          "  requirements  \"\", \"\", ...;\n"
          "  climate       \"\", \"\", ...;\n"
          "  terrain       \"\", \"\", ...;\n"
          "  system        D&D;\n"
          "  version       3rd;\n"
          "  world         ;\n"
          "  reference     ;\n"
          "  description   \n"
          "\n"
          "  \"\".\n\n")
  (my-insert-line (concat (make-string my-temp ?\ )
                          (string-trim '(?\ ) comment-start))
                  "" "left" ".")
  (forward-line -21)
  (forward-char 18)
)


(global-set-key [f5]  'my-insert-left-fold)
(global-set-key [f6]  'my-insert-center-fold)
(global-set-key [f7]  'my-insert-right-fold)
(global-set-key [f8]  'my-insert-DMA)
(global-set-key [f9]  'my-insert-copyright)
;; (global-set-key [f12]  'my-insert-icon-image)

(defun string-trim (bag s)
  "Returns a substring of the string specified by S that has had every
character in BAG removed from the beginning and end.  S must be a string or a
symbol.  If S is a symbol, its print name is used as the string.  The BAG
argument may be any sequence of characters.  Characters are trimmed from the
beginning and from the end of S until the first character not in BAG is
found."
  (let* ((len (length s))
	 (i1  (string-left-trim-index bag s 0 len))
	 (i2  (string-right-trim-index bag s len)))
    (if (<= i2 i1) "" (substring s i1 i2))))

; (string-trim '(?\ ?T) " Tasddf asdf af  ")
; (string-trim '(?\ ?\t) "  tBype asddf asdf af")

(defun string-left-trim (bag s)
  "Returns a substring of the string specified by S that has had every
character in BAG removed from the beginning.  S must be a string or a symbol.
The BAG argument may be any sequence of characters.  Characters are trimmed
from the beginning of S until the first character not in BAG is found."
  (let* ((len (length s))
	 (i1  (string-left-trim-index bag s 0 len)))
    (if (<= len i1) "" (substring s i1 len))))


(defun string-right-trim (bag s) 
  "Returns a substring of the string specified by S that has had every
character in BAG removed from the end.  S must be a string or a symbol.
The BAG argument may be any sequence of characters.  Characters are trimmed
from the end of S until the first character not in BAG is found."
  (let ((i2 (string-right-trim-index bag s (length s))))
    (if (<= i2 0) "" (substring s 0 i2))))


(defun string-left-trim-index (bag s i uplim)
  (if (or (eql i uplim)
	  (not (member (schar s i) bag)))
    i
    (string-left-trim-index bag s (1+ i) uplim)))


(defun string-right-trim-index (bag s i)
  (if (or (eql i 0)
	  (not (member (schar s (1- i)) bag)))
    i
    (string-right-trim-index bag s (1- i))))

(defun schar (s i)
  "Returns the ITH character of string S as a character object.  S must be
a simple string or a symbol.  If S is a symbol, its print name is used
as the string to operate on.  I must be a non-negative integer less than the
length of the string (indexing is zero-origin).  The function schar applied
to simple strings behaves identically to aref or char, but it may be faster
than either in many implementations."

  (string-to-char (substring s i)))
