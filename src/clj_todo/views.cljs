(ns clj-todo.views
  (:require
   [re-frame.core :as re-frame]
   [clj-todo.events :as events]
   [clj-todo.routes :as routes]
   [clj-todo.subs :as subs]
   ))


;; home

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [:div ]
     ]))

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
  [:div#header-links
   "[ "
   [:a {:on-click #(re-frame/dispatch [::events/navigate [:home]])}
     "Home"]
   " ]"])



;; main

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
  
  [:div header-links
    [:div (routes/panels @active-panel)]]))
