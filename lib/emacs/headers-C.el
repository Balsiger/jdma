;;
;; headers for C++ files
;;

(defun my-insert-C++-file-header ()
  "Inserts proper headers in a C++ file."
  (interactive)
  (beginning-of-buffer)
  (my-insert-line "//" "header" "right" "-")
  (insert "\n"
          "// \n"
          "// \n"
          "// $File:         " (file-name-nondirectory buffer-file-name) "\n"
          "// \n"
          "// $Author(s):    " user-full-name "\n"
          "// \n"
          "// $Responsible:  " user-full-name "\n"
          "// \n"
          "// $Desc:         \n"
          "// \n"
          "// $Distribution: \n"
          "// \n"
          "// $Bugs:         \n"
          "// \n"
          "// $To do:        \n"
          "// \n"
          "// $Keywords:     \n"
          "// \n"
          "// $See also:     \n"
          "// \n"
  )
  (insert "// \n"
          "// $Source: /home/cvsroot/jDMA/lib/emacs/headers-C.el,v $\n"
          "// \n"
          "// $Revision: 1.1 $\n"
          "// \n"
          "// $Locker:  $\n"
          "// $State: Exp $\n"
          "// $Log: headers-C.el,v $
          "// Revision 1.1  2002/08/27 16:00:12  merlin
          "// *** empty log message ***
          "//\n" 
          "//\n"
          "\n"
  )
  (my-insert-line "//" "" "right" ".")
  (insert "\n")
  (my-insert-line "//" "" "right" "_")
  (insert "\n")
  (if (string= "h" (file-name-extension buffer-file-name))
      (progn
       (insert "#ifndef _" 
               (file-name-sans-extension (file-name-nondirectory 
                                          buffer-file-name)) 
               "_h_\n"
               "#define _"
               (file-name-sans-extension (file-name-nondirectory 
                                          buffer-file-name)) 
               "_h_\n"
               "\n")
       (my-insert-line "//" "includes" "right" "-")
       (insert "\n"
               "#ifndef _DMA_h_\n"
               "#include \"DMA.h\"\n"
               "#endif\n"
               "\n")
       (my-insert-line "//" "" "right" ".")
       (insert "\n"
               "namespace DMA\n"
               "{\n"
               "\n")
       (my-insert-line "//" "prototypes" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n"
               "//\n"
               "// $Desc:         \n"
               "//\n"
               "// $Pattern:      \n"
               "//\n"
               "// $Is A:         \n"
               "// $Communicates: \n"
               "// $Uses:         \n"
               "// $Has A:        \n"
               "// $Holds A:      \n"
               "// $Was A:        \n"
               "// \n"
               "// $Example:      \n"
               "//\n"
               "// $Derivation:   \n"
               "//\n"
               "// $Bugs:         \n"
               "//\n"
               "// $To Do:        \n"
               "// \n"
               "// $Keywords:     \n"
               "// \n"
               "// $See Also:     \n"
               "//\n"
               "//\n"
               "class " 
               (file-name-sans-extension (file-name-nondirectory 
                                          buffer-file-name)) 
               " : public Debug<" 
               (file-name-sans-extension (file-name-nondirectory 
                                          buffer-file-name)) 
               ">\n"
               "{\n"
       )
       (my-insert-line "  //" "friends" "right" "-")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "enums" "right" "-")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "constructor(s)" "right" "-")
       (insert "\n"
               "  public:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "destructor" "right" "-")
       (insert "\n"
               "  public:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "variables" "right" "-")
       (insert "\n"
               "  private:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "manipulators" "right" "-")
       (insert "\n"
               "  public:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "accessors" "right" "-")
       (insert "\n"
               "  public:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n")
       (my-insert-line "  //" "other member functions" "right" "-")
       (insert "\n"
               "  public:\n"
               "\n")
       (my-insert-line "  //" "" "right" ".")
       (insert "\n"
               "};\n"
               "\n")
       (my-insert-line "//" "template/inline functions" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "nonmember functions" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n"
               "}\n"
               "\n"
               "#endif\n"
       )
      )
      (progn
       (my-insert-line "//" "includes" "right" "-")
       (insert "\n"
               "#include \"" 
               (file-name-sans-extension (file-name-nondirectory 
                                          buffer-file-name)) 
               ".h\"\n"
               "\n")
       (my-insert-line "//" "" "right" ".")
       (insert "\n"
               "namespace DMA\n"
               "{\n"
               "\n")
       (my-insert-line "//" "globals" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "prototypes" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "constructor(s), destructor" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "manipulators" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "accessors" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "other member functions" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n")
       (my-insert-line "//" "nonmember functions" "right" "-")
       (my-insert-line "//" "" "right" ".")
       (insert "\n"
               "}\n"
               "\n")
      )
  )
)

(defun my-insert-C++-func-header ()
  "Insert a C++ function header"
  (interactive)
  (beginning-of-line)
  (set 'my-temp (read-from-minibuffer "Function Name: "))
  (my-insert-line "//" my-temp "center" "-")
  (insert "\n"
          "//\n"
          "// $Desc:       \n"        
          "//\n"
          "// $Arguments:  \n"
          "//\n"
          "// $Return:     \n"
          "//\n"
          "// $Undefined:  \n"
          "//\n"
          "// $Algorithm:  \n"
          "//\n"
          "// $Derivation: \n"
          "//\n"
          "// $Example:    \n"
          "//\n"
          "// $Bugs:       \n"
          "//\n"
          "// $To Do:      \n"
          "//\n"
          "// $Keywords:   \n"
          "//\n"
          "// $See Also:   \n"
          "//\n"
          "//\n"
          "\n"
          "\n"
          (file-name-sans-extension (file-name-nondirectory buffer-file-name))
          "::" my-temp "()\n"
          "{\n"
          "\n"
          "}\n"
          "\n"
  )     
  (my-insert-line "//" "" "" ".")
  (previous-line 4)
  (insert "  ")
)



