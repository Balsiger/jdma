;;
;; headers for html files
;;

(defun my-insert-HTML-file-header ()
  "Inserts proper headers in a html file."
  (interactive)
  (beginning-of-buffer)
  (insert "<* \n"
          " * \n"
          " * $File:         " (file-name-nondirectory buffer-file-name) "\n"
          " * \n"
          " * $Author(s):    " user-full-name "\n"
          " * \n"
          " * $Responsible:  " user-full-name "\n"
          " * \n"
          " * $Desc:         \n"
          " * \n"
          " * $Distribution: \n"
          " * \n"
          " * $Bugs:         \n"
          " * \n"
          " * $To do:        \n"
          " * \n"
          " * $Keywords:     \n"
          " * \n"
          " * $See also:     \n"
          " * \n"
  )
  (my-insert-line " *" "RCS" "right" "-")
  (insert " * \n"
          " * $Source: /home/cvsroot/jDMA/lib/emacs/headers-html.el,v $\n"
          " * \n"
          " * $Revision: 1.1 $\n"
          " * \n"
          " * $Locker:  $\n"
          " * $State: Exp $\n"
          " * $Log: headers-html.el,v $
          " * Revision 1.1  2002/08/27 16:00:12  merlin
          " * *** empty log message ***
          " *\n"
          " *\n"
          " *\n"
  )
  (my-insert-line " *" "" "right" ".")
  (insert " *\n"
          " *>\n"
          "\n")
)

(defun my-insert-HTML-func-header ()
  "Insert a html function header"
  (interactive)
  (beginning-of-line)
  (set 'my-temp (read-from-minibuffer "Function Name: "))
  (my-insert-line "<*" my-temp "center" "-")
  (delete-char -3)
  (insert "*>\n"
          "\n"
          "<*\n"
          " * $Desc:       \n"        
          " *\n"
          " * $Arguments:  \n"
          " *\n"
          " * $Return:     \n"
          " *\n"
          " * $Undefined:  \n"
          " *\n"
          " * $Algorithm:  \n"
          " *\n"
          " * $Derivation: \n"
          " *\n"
          " * $Example:    \n"
          " *\n"
          " * $Bugs:       \n"
          " *\n"
          " * $To Do:      \n"
          " *\n"
          " * $Keywords:   \n"
          " *\n"
          " * $See Also:   \n"
          " *\n"
          " *>\n"
          "\n"
          "<$MACRO " my-temp ">\n"
          "\n"
          "</MACRO>\n"
          "\n"
  )     
  (my-insert-line "<*" "" "" ".")
  (delete-char -3)
  (insert "*>")
  (beginning-of-line)
  (previous-line 3)
  (insert "  ")
)



