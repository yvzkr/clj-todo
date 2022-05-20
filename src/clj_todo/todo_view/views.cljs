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
    [:div.field
     [:label.label label]
     [:div.control
      [:input.input {:value @value
                     :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                     :type "text"
                     :placeholder "Text input"}]]]))
    
    
(defn textarea-input [id label]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:div.field
     [:label.label label]
     [:div.control
      [:textarea {:value @value
                  :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                  :class "textarea"
                  :placeholder "Text Input"}]]]))


(defn edit-todo-form []
  (let [is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:title]])
        request-update-error (re-frame/subscribe [::subs/updated-error])
        request-update-success (re-frame/subscribe [::subs/request-update-success])
        ]
    [:div {:class "create-todo-form"}
     [:div {:class "todo-edit-header"}
      [:h1 {:class "page-title"} (str "Edit Todo")]]

     [:div {:class "error-div"}
      (when @request-update-error
        [:div {:class "notification is-danger"}
         [:h1 (str "Error Update Todo")]
         [:button {:class "delete" :on-click #(re-frame/dispatch [::events/clear-update-todo-error])}]])]

     [:div {:class "success-div"}
      (when @request-update-success
        [:div {:class "notification is-success"}
         [:h1 (str "Success Update Todo")]
         [:button {:class "delete" :on-click #(re-frame/dispatch [::events/clear-update-todo-success])}]])]
     
     [:div {:class "todo-edit-body"}
      [text-input :title "Title"]
      [textarea-input :remark "Remark"]]

     [:div {:class "todo-edit-footer"}
      [:button.button.is-danger
       {:disabled (not is-valid?)
        :on-click (fn []
                    (re-frame/dispatch [::events/close-edit-form])
                    (re-frame/dispatch [::route-events/navigate [:todos-index]]))}
       "Back"]
      [:button.button.is-primary
       {:disabled (not is-valid?)
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
        [:div
         [edit-todo-form]])})))

    

(defn todo-view []
  (let [route-params @(re-frame/subscribe [::route-subs/route-params])
        todo @(re-frame/subscribe [::subs/todo (:id route-params)])]
    [:div {:class "edit-container"}
     
     [todo-view-component todo]]))


(defmethod routes/panels :todo-view-panel [] [todo-view])
