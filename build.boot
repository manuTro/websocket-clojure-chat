
(set-env!
 :source-paths #{"src/clj" "src/cljs"}
 :resource-paths #{"html"}


:dependencies '[
                 [org.clojure/clojure "1.8.0"]         ;; add CLJ
                 [org.clojure/clojurescript "1.9.473"] ;; add CLJS
                 [adzerk/boot-cljs "1.7.228-2"]
                 [pandeiro/boot-http "0.7.6"]
                 [adzerk/boot-reload "0.5.1"]
                 [adzerk/boot-cljs-repl "0.3.0"]       ;; add bREPL
                 [com.cemerick/piggieback "0.2.1"]     ;; needed by bREPL
                 [weasel "0.7.0"]
                 [hiccup "1.0.5"]
                 [http-kit "2.2.0"]                   ;; needed by bREPL
                 [org.clojure/tools.nrepl "0.2.12"]    ;; needed by bREPL
                 [org.clojars.magomimmo/domina "2.0.0-SNAPSHOT"]
                 [hiccups "0.3.0"]
                 [compojure "1.5.2"]
                 [com.taoensso/timbre "4.8.0"]
                 [ring/ring-defaults "0.2.1"]
                 [org.clojure/tools.logging "0.3.1"]               ;; for routing
                ;  [org.clojars.magomimmo/shoreleave-remote-ring "0.3.3"]
                ;  [org.clojars.magomimmo/shoreleave-remote "0.3.1"]
                 ])

 (require '[adzerk.boot-cljs :refer [cljs]]
          '[pandeiro.boot-http :refer [serve]]
          '[adzerk.boot-reload :refer [reload]]
          '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

  (deftask dev
    "Launch immediate feedback dev environment"
    []
    (comp
     (serve :handler 'server/all-routes             ;; ring hanlder
            :resource-root "target"
            :httpkit true                    
            :reload true)                                ;; reload ns
     (watch)
     (reload)
     (cljs-repl) ;; before cljs
     (cljs)
     (target :dir #{"target"})))
