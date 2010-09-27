;;
;; headers for perl files
;;

(defun my-insert-Perl-file-header ()
  "Inserts proper headers in a perl file"
  (interactive)
  (beginning-of-buffer)
  (insert "# \n"
          "# \n"
          "# $File:         " (file-name-nondirectory buffer-file-name) "\n"
          "# \n"
          "# $Author(s):    " user-full-name "\n"
          "# \n"
          "# $Responsible:  " user-full-name "\n"
          "# \n"
          "# $Desc:         \n"
          "# \n"
          "# $Distribution: \n"
          "# \n"
          "# $Bugs:         \n"
          "# \n"
          "# $To do:        \n"
          "# \n"
          "# $Keywords:     \n"
          "# \n"
          "# $See also:     \n"
          "# \n"
  )
  (my-insert-line "#" "RCS" "right" "-")
  (insert "# \n"
          "# $Source: /home/cvsroot/jDMA/lib/emacs/headers-perl.el,v $\n"
          "# \n"
          "# $Revision: 1.2 $\n"
          "# \n"
          "# $Locker:  $\n"
          "# $State: Exp $\n"
          "# $Log: headers-perl.el,v $
          "# Revision 1.2  2008/02/24 15:59:07  merlin
          "# *** empty log message ***
          "#
          "# Revision 1.1  2002/08/27 16:00:12  merlin
          "# *** empty log message ***
          "#\n"
          "#\n"
          "#\n"
  )
  (my-insert-line "#" "" "right" ".")
  (insert "#\n"
          "#\n"
          "\n")
  (my-insert-line "#" "" "right" "_")
  (insert "\n")
)

(defun my-insert-Perl-func-header ()
  "Insert a perl function header"
  (interactive)
  (beginning-of-line)
  (set 'my-temp (read-from-minibuffer "Function Name: "))
  (my-insert-line "#" my-temp "center" "-")
  (insert "\n"
          "#\n"
          "# $Desc:       \n"        
          "#\n"
          "# $Arguments:  \n"
          "#\n"
          "# $Return:     \n"
          "#\n"
          "# $Undefined:  \n"
          "#\n"
          "# $Algorithm:  \n"
          "#\n"
          "# $Example:    \n"
          "#\n"
          "# $Bugs:       \n"
          "#\n"
          "# $To Do:      \n"
          "#\n"
          "# $Keywords:   \n"
          "#\n"
          "# $See Also:   \n"
          "#\n"
          "#\n"
          "\n"
          "\n"
          "sub " my-temp "()\n"
          "{\n"
          "\n"
          "}\n"
          "\n"
  )     
  (my-insert-line "#" "" "" ".")
  (previous-line 4)
  (insert "  ")
)



