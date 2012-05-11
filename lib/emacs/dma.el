;;
;; The macros and settings used for DMA development with emacs.
;;
;;

;;
;; add the paths from where to load emacs lisp progras
;;
(setq load-path (cons "~/jDMA/svn/lib/emacs" load-path))
(setq load-path (cons "~/private/jDMA/svn/lib/emacs" load-path))

;;
;; Show the linenumber
;;
(setq line-number-mode 1)

;;
;; number the columns
;;
(column-number-mode t)

;;
;; add CTRL-X-C for quit again
;;
(defun my-exit-from-emacs ()
  (interactive)
  (if (yes-or-no-p "Quit Emacs ? ")
      (save-buffers-kill-emacs)
  )
)
(global-set-key "\C-x\C-c" 'my-exit-from-emacs)

;;
;; turn font locking on
;;
(global-font-lock-mode t)
(setq font-lock-maximum-decoration t)

;;
;; set the default fill column to 79 characters
;;
(setq-default fill-column 79)

;;
;; add key for goto-line
;;
(global-set-key [f1] 'goto-line)

;;
;; remove spaces right of cursor up to next word (excluding word)
;;
(fset 'remove_spaces "\C-@\ef\eb\C-w")
(global-set-key [f2] 'remove_spaces)

;;
;; execute keyboard macro
;;
(global-set-key [f12] 'call-last-kbd-macro)

;;
;; Folding Mode
;;

(setq auto-mode-alist
      (append  '(("\\.pl$" . perl-mode))
       auto-mode-alist))
(setq auto-mode-alist
      (append  '(("\\.pm$" . perl-mode))
       auto-mode-alist))
(setq auto-mode-alist
      (append  '(("\\.dma$" . text-mode))
       auto-mode-alist))
(setq auto-mode-alist
      (append  '(("\\.js$" . java-mode))
       auto-mode-alist))

(make-variable-buffer-local 'comment-fold)
(add-hook 'java/l-mode-hook
          '(lambda ()
             (setq comment-fold "//")
             ))
(add-hook 'java-mode-hook
          '(lambda ()
             (setq comment-fold "//")
             ))
(add-hook 'java-mode-hook 'java-mode-indent-annotations-setup)
(add-hook 'text-mode-hook
          '(lambda ()
             (setq comment-fold "#")
             ))

(load "fold")
(setq fold-autoclose-other-folds nil)
(add-hook 'fold-mode-hook
          '(lambda ()
             (define-key fold-mode-map [S-right] 'fold-show)
             (define-key fold-mode-map [S-left] 'fold-hide)
             (fold-set-marks comment-fold nil "----[-]+" "....[.]+" nil)
             ))


;;
;; fill a line with the last character of the line
;;
(defun fill-line2 ()
  ""
  (interactive)
  (if (not (= (current-column) 0))
      (while (< (current-column) fill-column)
        (insert (preceding-char))
        )
    )
  )
(global-set-key "\C-cf" 'fill-line2)

;;
;; reload all folds
;;
(defun reload-folds ()
  ""
  (interactive)
  (fold-mode)
  (fold-mode)
  )
(global-set-key [f11] 'reload-folds)

;;
;; set marking mode transient to true
;;
(setq transient-mark-mode t)

;;
;; annotation indentation
;;
(require 'java-mode-indent-annotations)


;;
;; C-Style
;;
(setq c-indent-level               2)
(setq c-continued-statement-offset 2)
(setq c-brace-offset               -2)
(setq c-argdecl-indent             0)
(setq c-label-offset               -2)
(setq c-auto-newline               0)
(setq c-basic-offset               2)


(setq-default indent-tabs-mode nil)

;; load cc-mode
(require 'cc-mode)

(if (<= 20 (symbol-value 'emacs-major-version))
    (c-initialize-cc-mode)
)

;; define my own style
(defconst my-c++-style
  '((c-basic-offset . 2)
    (c-comment-only-line-offset . (0 . 0))
    (c-hanging-braces-alist     . ((substatement-open before after)))
    (comment-column . 46)
    (c-auto-newline . nil)
    (c-offsets-alist . ((knr-argdecl-intro     . 5)
                        (statement-block-intro . +)
                        (substatement-open     . 0)
                        (label                 . 0)
                        (statement-case-open   . 0)
                        (statement-case-intro  . +)
                        (case-label            . +)
                        (statement-cont        . +)
                        (arglist-intro . c-lineup-arglist-intro-after-paren)
                        (arglist-close . c-lineup-arglist)
                        (access-label  . -)
                        (inclass . ++)
                        (innamespace . 0)
                        (member-init-intro . 0)))
    )
  "My C++ Programming Style")

;; define my own style
(defconst my-java-style
  '((c-basic-offset . 2)
    (c-comment-only-line-offset . (0 . 0))
    (c-hanging-braces-alist     . ((substatement-open before after)))
    (comment-column . 46)
    (c-auto-newline . nil)
    (c-offsets-alist . ((knr-argdecl-intro     . 5)
                        (statement-block-intro . +)
                        (substatement-open     . 0)
                        (label                 . 0)
                        (statement-case-open   . 0)
                        (statement-case-intro  . +)
                        (case-label            . +)
                        (statement-cont        . +)
                        (topmost-intro-cont    . +)
                        (arglist-intro . c-lineup-arglist-intro-after-paren)
                        (arglist-close . c-lineup-arglist)
                        (access-label  . -)
                        (inclass . +)
                        (innamespace . 0)
                        (member-init-intro . 0)
                        (inline-open . 0)))
    )
  "My Java Programming Style")

;; Customizations for all of c-mode, c++-mode, and objc-mode
(defun my-c-mode-common-hook ()
  ;; add my personal style and set it for the current buffer
  (c-add-style "c++" my-c++-style t)
  (c-add-style "java" my-java-style t)
)

;; add my definition of java mode
(c-add-style "c++" my-c++-style t)
(c-add-style "java" my-java-style t)

;; the following only works in Emacs 19
;; Emacs 18ers can use (setq c-mode-common-hook 'my-c-mode-common-hook)
(add-hook 'c-mode-common-hook 'my-c-mode-common-hook)

(setq auto-mode-alist
      (append  '(("\\.h$" . c++-mode)) '(("\\.hsc$" . HTML-mode))
       auto-mode-alist)
)


;;
;; add headers
;;
(load "headers.el")


;;
;; SGML Mode (HTML)
;;
(setq sgml-indent-data 1)

;; JSP mode
(autoload 'multi-mode
  "multi-mode"
  "Allowing multiple major modes in a buffer."
  t)

(defun jsp-mode () (interactive)
  (multi-mode 1
              'html-mode
              ;;your choice of modes for java and html
              ;;'("<%" java-mode)
              '("<%" jde-mode)
              '("%>" html-mode)))

(setq auto-mode-alist
      (cons '("\\.jsp$" . jsp-mode)
            auto-mode-alist))

;;
;; set the preffered coding system to utf-8
;;
(setq locale-coding-system 'utf-8)
(set-terminal-coding-system 'utf-8)
(set-keyboard-coding-system 'utf-8)
(set-selection-coding-system 'utf-8)
(prefer-coding-system 'utf-8)

;;
;; enable unique buffer names
;;
(require 'uniquify)
(setq uniquify-buffer-name-style 'post-forward-angle-brackets)

;;
;; delete trailing whitespace
;;
(add-hook 'before-save-hook 'delete-trailing-whitespace)

;;
;; soy mode
;;
(load-file "~/private/jDMA/svn/lib/emacs/soy-mode.el")
(require 'soy-mode)
