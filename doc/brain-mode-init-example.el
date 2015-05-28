;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Emacs environment (essential)

;; add the directory containing Brain-mode and other libs
(let ((default-directory "~/.emacs.d/elisp/"))
      (normal-top-level-add-subdirs-to-load-path))

;; load the library
(require 'brain-mode)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rexster URL and Extend-o-Brain graph (essential)

(defvar exo-rexster-url "http://localhost:8182")
(defvar exo-rexster-graph "arthurkb")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; data importing and exporting

;; export the graph to here, or populate an empty graph from here
(defvar exo-default-graphml-file "/Volumes/encrypted/personal-git-repo/arthurkb.xml")

;; RDF inference over entire graph is written here
(defvar exo-default-rdf-file "/tmp/arthurkb.nt")

;; RDF inference over the public portion of the graph is written here
;; using Dropbox and Apache+.htaccess allows it to be automatically published as Linked Data
(defvar exo-default-webrdf-file "/Users/arthur/Dropbox/shared/domains/www.fortytwo.net/people/arthur/extend-o-brain.rdf")

;; default location for dumps of tab-separated vertex and edge files
;; vertex files contain the properties of each atom
;; edge files are parent/child adjacency lists
(defvar exo-default-vertices-file "/tmp/arthurkb-vertices.tsv")
(defvar exo-default-edges-file "/tmp/arthurkb-edges.tsv")

;; default location for PageRank results
(defvar exo-default-pagerank-file "/tmp/arthurkb-pagerank.tsv")


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; other settings useful with Brain-mode

;; tabs as 4-spaces
(setq-default indent-tabs-mode nil)
(setq-default tab-width 4)

(setq-default truncate-lines t)
(if full-colors-supported
    (global-hl-line-mode 1))

