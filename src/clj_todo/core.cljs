(ns clj-todo.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [clj-todo.events :as events]
   [clj-todo.routes :as routes]
   [clj-todo.views :as views]
   [clj-todo.config :as config]
   [clj-todo.todos-index.views]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (routes/start!)
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
