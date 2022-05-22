(ns clj-todo.views
  (:require
   [re-frame.core :as re-frame]
   [clj-todo.events :as events]
   [clj-todo.routes :as routes]
   [clj-todo.subs :as subs]
   [reagent.core :as reagent]
   ))

(defn success-api-url-noti []
  (let [error (re-frame/subscribe [::subs/success-update-api-url])]
    (when @error
      [:div {:class "alert alert-success alert-dismissible todo-margin-top"}       
       [:span (str "Updated Api Url")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-success-update-api-url])} "x"]])))



(defn api-url-update-button []
  [:button {
            :class "btn btn-primary mb-4 float-right"
            :on-click #(re-frame/dispatch [::events/load-new-api-url-to-api-url])} "Update"])

(defn api-url-input [label]
  (let [value (re-frame/subscribe [::subs/new-api-url])]
    [:input.input {:value @value
                   :class "form-control api-url-input"
                   :on-change #(re-frame/dispatch [::events/update-api-url-text (-> % .-target .-value)])
                   :type "text"
                   :placeholder label}]))


(defn api-url-update-form []
  (let []
    [:div {:class "form-inline"}
      [:h1 {:class "page-title"} "You can change Api Url"]
     [:div {:class "form-group"}
      [:label {:class "sr-only"} "Api url"]
      [api-url-input "Update api url"]]
     [api-url-update-button]]))




(defn update-api-url-component []
  (let [state (reagent/atom {})] ;; you can include state
    (reagent/create-class
     {:component-did-mount
      (fn []
        (re-frame/dispatch [::events/load-api-url-form])
        (re-frame/dispatch [::events/clear-success-update-api-url])
        (println "I am alive.. ❤️ ❤️ ❤️ ❤️ ❤️ "))

       ;; ... other methods go here
       ;; println state

       ;; name your component for inclusion in error messages
      :display-name "todos-main-component"

       ;; note the keyword for this method
      :reagent-render
      (fn []
        [:div {:class "row"}
         [:div {:class "col-md-5 col-md-offset-4 col-xs-10"}         
          [api-url-update-form]
          [success-api-url-noti]
         ]])})))





;; home [display-update-name-button]

(defn home-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div {:class "container"}
     [:h1
      (str "Hello from " @name ". This is the Home Page.")]
     [update-api-url-component]]))

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
