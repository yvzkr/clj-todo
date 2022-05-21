(ns clj-todo.todos-index.views
  (:require [re-frame.core :as re-frame]
            [clj-todo.todos-index.subs :as subs]
            [clj-todo.routes :as routes]
            [clj-todo.events :as route-events]
            [clj-todo.todos-index.events :as events]
            [reagent.core :as reagent]))
;
;[:a {:on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]])}
;     "Edit"]
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


(defn request-created-error []
  (let [error (re-frame/subscribe [::subs/created-error])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Error Create Item")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-create-todo-error])} "x"]])))


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
      [request-created-error]]]))


(defn error-request-delete-alert []
  (let [request-delete-todo-error (re-frame/subscribe [::subs/request-delete-todo-error])]
    (when @request-delete-todo-error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Error Delete Item")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-request-delete-todo-error])} "x"]])))


(defn error-request-todos-alert []
  (let [error (re-frame/subscribe [::subs/error-request-todos])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Error Request Todo List. Please Check Api Url")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-request-todos-error])} "x"]])))



;;main todo-index
(defn todo-list []
  (let [todos (re-frame/subscribe [::subs/todos])
        loading (re-frame/subscribe [::subs/loading])]
    [:div {:class ""}
     [:div
      [:h1 {:class "page-title"} (str "Todo List")]      
      (when @loading "Loading...")
      [error-request-delete-alert]
      [error-request-todos-alert]
      [:table {:class "table table-striped table-hover "}
       [:thead {:class "thead-dark table-header"}
        [:tr  {:scope "col" }
         [:th "Title"] [:th "Status"] [:th  [fetch-todos-button]]]]
       [:tbody
        (map display-todo @todos)]]]]))

(defn todos-main-component []
  (let [state (reagent/atom {})] ;; you can include state
    (reagent/create-class
     {:component-did-mount
      (fn []
        (re-frame/dispatch [::events/fetch-todos])
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
