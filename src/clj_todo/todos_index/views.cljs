(ns clj-todo.todos-index.views
  (:require [re-frame.core :as re-frame]
            [clj-todo.todos-index.subs :as subs]
            [clj-todo.routes :as routes]            
            [clj-todo.todos-index.events :as events]
            [reagent.core :as reagent]))
;
;[:a {:on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]])} "Edit"]
;     
;   " | "

(defn checkbox [name checked?]
  [:input {:type :checkbox
           :checked (boolean checked?)
           :on-change #(println checked? "değişti")
           :name name}])

;[:i {:class (if status "fas fa-check-square" "fas fa-star-o") }  "[x]"] 

(defn display-todo [{:keys [id title status]}]
  [:tr {:key id :class (if status "completed" " ")}
   [:td  title]
   [:td  (if status "Done" "Not Done")]
   [:td
    
    [:a {:on-click #(re-frame/dispatch [::events/request-delete-todo id])}
     [:i {:class "fas fa-trash"}]]
    " | "
    [:a {:on-click #(re-frame/dispatch [::events/request-todo-done id (not status)])}
     [checkbox " " status]  ]]])

(defn fetch-todos-button []
  [:button {:class "btn btn-success"
            :on-click #(re-frame/dispatch [::events/fetch-todos])}
   [:i {:class "fas fa-retweet"}]])

(defn text-input [id label]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:input.input {:value @value
                   :class "form-control"
                   :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                   :type "text"
                   :placeholder label}]))


(defn alert-error-request-create-todo-div []
  (let [error (re-frame/subscribe [::subs/error-request-create-todo])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Request Error Create Todo")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-error-request-create-todo])} "x"]])))

(defn alert-error-request-delete-todo-div []
  (let [error (re-frame/subscribe [::subs/error-request-delete-todo])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}
       [:span (str "Error Request Delete Todo")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-error-request-delete-todo])} "x"]])))

(defn alert-error-request-todos-div []
  (let [error (re-frame/subscribe [::subs/error-request-todos])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Error Request Todo List. Please Check Api Url")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-error-request-todos])} "x"]])))







(defn new-todo-form []
  (let [is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:title]])]
    [:div {:class "form-group "}
     [:h1 {:class "page-title"} (str "😴")]

     [:div {:class "todo-add-form-element-div"}
      [text-input :title "Add Items....."]]
     [:div {:class "todo-add-form-element-div"}
      [:button.button
       {:disabled (not is-valid?)
        :class "form-control btn btn-success todo-create-btn"
        :on-click #(re-frame/dispatch [::events/request-create-todo])}
       "Add"]]
     [:div {:class "todo-add-form-element-div"}
      [alert-error-request-create-todo-div]]]))

;;main todo-index
(defn todo-list []
  (let [todos (re-frame/subscribe [::subs/todos])
        loading (re-frame/subscribe [::subs/loading])]
    [:div {:class ""}
     [:div
      [:h1 {:class "page-title"} (str "Todo List")]
      [alert-error-request-delete-todo-div]
      [alert-error-request-todos-div]
      [:table {:class "table table-striped table-hover todo-table"}
       [:thead {:class "thead-dark table-header"}
        [:tr  {:scope "col"}
         [:th {:class "table-th-text"} "Title"]
         [:th {:class "table-th-status"} "Status"]
         [:th {:class "table-th-settings"} [fetch-todos-button]]]]
       [:tbody
        (map display-todo @todos)]]
      (when @loading "Loading...")]]))

(defn todos-main-component []
  #_{:clj-kondo/ignore [:unused-binding]}
  (let [state (reagent/atom {})] ;; you can include state
    (reagent/create-class
     {:component-did-mount
      (fn []
        (re-frame/dispatch [::events/fetch-todos])
        (re-frame/dispatch [::events/clear-all-alert-message])
        (println "I am alive.. ❤️ ❤️ ❤️ ❤️ ❤️ "))

       ;; ... other methods go here
       ;; println state

       ;; name your component for inclusion in error messages
      :display-name "todos-main-component"

       ;; note the keyword for this method
      :reagent-render
      (fn []
        [:div {:class "row"}
         [:div {:class "col-md-8 col-md-offset-2 col-xs-10"}
          [new-todo-form]
          [todo-list]]])})))



(defn todos-index []
  [:div {:class "container"}
   [todos-main-component]])


(defmethod routes/panels :todos-index-panel [] [todos-index])
