(ns clj-todo.todo-view.views
  (:require [clj-todo.events :as route-events]
            [clj-todo.routes :as routes]
            [clj-todo.subs :as route-subs]
            [clj-todo.todo-view.events :as events]
            [clj-todo.todo-view.subs :as subs]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))



(defn text-input [id label]
  (let [value (re-frame/subscribe [::subs/form id])]
      [:input.input {:value @value
                     :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                     :type "text"
                     :class "form-control"
                     :placeholder label}]))


(defn request-update-error-noti []
  (let [error (re-frame/subscribe [::subs/updated-error])]
    (when @error
      [:div {:class "alert alert-danger alert-dismissible "}       
       [:span (str "Error Update Item")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-update-todo-error])} "x"]])))


(defn request-update-success-noti []
  (let [error (re-frame/subscribe [::subs/request-update-success])]
    (when @error
      [:div {:class "alert alert-success alert-dismissible "}       
       [:span (str "Updated Item")]
       [:button {:class "close" :on-click #(re-frame/dispatch [::events/clear-update-todo-success])} "x"]])))




(defn edit-todo-form []
  (let [is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:title]])]
    [:div {:class "form-group "}
     [:h1 {:class "page-title"} (str "Edit Todo")]

     [request-update-error-noti]

     [request-update-success-noti]

     [:div {:class "todo-add-form-element-div"}
      [text-input :title "Title"]]

     [:div {:class "todo-add-form-element-div"}
      [:button.button.is-danger
       {:disabled (not is-valid?)
        :class "form-control btn btn-danger"
        :on-click (fn []
                    (re-frame/dispatch [::events/close-edit-form])
                    (re-frame/dispatch [::route-events/navigate [:todos-index]]))}
       "Back"]]


     [:div {:class "todo-add-form-element-div"}
      [:button.button.is-primary
       {:disabled (not is-valid?)
        :class "form-control btn btn-success"
        :on-click #(re-frame/dispatch [::events/request-update-todo])}
       "Update"]]]))


(defn todo-view-component [todo]
  (let [state (reagent/atom {})]
    (reagent/create-class
     {:component-did-mount
      (fn []
        (println "I am fine..")
        (re-frame/dispatch [::events/load-todo-edit-form todo])
        (re-frame/dispatch [::events/clear-update-todo-success])
        (re-frame/dispatch [::events/clear-update-todo-error])        
        )

       ;; ... other methods go here

       ;; name your component for inclusion in error messages
      :display-name "todo-view-component"

       ;; note the keyword for this method
      :reagent-render
      (fn []
        [:div {:class "row"}
        [:div {:class "col-md-8 col-md-offset-2 col-xs-10"}
         [edit-todo-form]]])})))

    

(defn todo-view []
  (let [route-params @(re-frame/subscribe [::route-subs/route-params])
        todo @(re-frame/subscribe [::subs/todo (:id route-params)])]
    [:div {:class "container"}
     
     [todo-view-component todo]]))


(defmethod routes/panels :todo-view-panel [] [todo-view])
