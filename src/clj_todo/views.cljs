(ns clj-todo.views
  (:require
   [re-frame.core :as re-frame]
   [clj-todo.events :as events]
   [clj-todo.routes :as routes]
   [clj-todo.subs :as subs]))

(defn display-update-name-button []
  [:button {:on-click #(re-frame/dispatch [::events/update-name "yesyes"])} "update name"])



;; home

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [:div
      [display-update-name-button]]]))

(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-panel []
  [:div
   [:h1 "This is the About Page."]
   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])}
     "go to Home Page"]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; header

(def header-links
  [:div#header-links {:class "header-menu"}
   "[ "
   [:a {:on-click #(re-frame/dispatch [::events/navigate [:home]])}
    "Home"]
   " | "
   [:a {:on-click #(re-frame/dispatch [::events/navigate [:todos-index]])}
    "Todos"]
   " ]"])


;; main

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [:div 
     [:div {:class "wrapper"} header-links]
     [:div {:class "page-content"} (routes/panels @active-panel)]]
    ))
