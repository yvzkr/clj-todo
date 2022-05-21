(ns clj-todo.views
  (:require
   [re-frame.core :as re-frame]
   [clj-todo.events :as events]
   [clj-todo.routes :as routes]
   [clj-todo.subs :as subs]))

(defn display-update-name-button []
  [:button {:on-click #(re-frame/dispatch [::events/update-name "yesyes"])} "update name"])



;; home [display-update-name-button]

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div {:class "container"}
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [:div
      ]]))

(defmethod routes/panels :home-panel [] [home-panel])

;; about

(defn about-panel []
  [:div {:class "container"}
   [:h1 "This is the About Page."]
   [:div
    [:a {:on-click #(re-frame/dispatch [::events/navigate :home])}
     "go to Home Page"]]])

(defmethod routes/panels :about-panel [] [about-panel])

;; header

(def header-links
  [:div#header-links {:class "menu-wrapper menu-gold"}
   [:h1 "Clojure Todo App"]
   [:ul {:class "menu"}
    [:li
     [:a {:on-click #(re-frame/dispatch [::events/navigate [:home]])}
      [:i {:class "fas fa-home"}] " Home"]]
    [:li
     [:a {:on-click #(re-frame/dispatch [::events/navigate [:todos-index]])}
      [:i {:class "fas fa-tasks"}] " Todos"]]
    ]])


;; main

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    [:div 
     [:div {:class "wrapper"} header-links]
     [:div {:class "page-content"} (routes/panels @active-panel)]]
    ))
