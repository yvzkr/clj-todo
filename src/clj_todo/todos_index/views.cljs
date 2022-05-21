(ns clj-todo.todos-index.views
  (:require [re-frame.core :as re-frame]
            [clj-todo.todos-index.subs :as subs]
            [clj-todo.routes :as routes]
            [clj-todo.events :as route-events]
            [clj-todo.todos-index.events :as events]
            [reagent.core :as reagent]))




(defn display-todo [{:keys [id title status]}]
  [:tr {:key id :class (if status "completed" " ")}
   [:td  title]
   [:td  (if status "done" "not done")]
   [:td
    [:a {:on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]])}
     "Edit"]
    " | "
    [:a {:on-click #(re-frame/dispatch [::events/request-delete-todo id])}
     "Delete"]
    " | "
    [:a {:on-click #(re-frame/dispatch [::events/request-todo-done id (not status)])}
     (if status "Done" "Not Done")]]])

(defn fetch-todos-button []
  [:button {:on-click #(re-frame/dispatch [::events/fetch-todos]) :class "refresh-btn"} "Refresh"])

(defn text-input [id label]
  (let [value (re-frame/subscribe [::subs/form id])]
    [:div.field
     [:label.label label]
     [:div.control
      [:input.input {:value @value
                     :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])
                     :type "text"
                     :placeholder "Text input"}]]]))


(defn new-todo-form []
  (let [is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:title]])
        created-error (re-frame/subscribe [::subs/created-error])]
    [:div {:class "create-todo-form"}
     [:h1 {:class "page-title"} (str "Create New Todo")]

     [text-input :title "Title"]
     (when @created-error
       [:div {:class "notification is-danger"}
        [:h1 (str "Error create Todo")]
        [:button {:class "delete" :on-click #(re-frame/dispatch [::events/clear-create-todo-error])} "Done"]])

     [:button.button.is-primary
      {:disabled (not is-valid?)
       :on-click #(re-frame/dispatch [::events/request-create-todo])}
      "Save"]]))

;;main todo-index
(defn todo-list []
  (let [todos (re-frame/subscribe [::subs/todos])
        loading (re-frame/subscribe [::subs/loading])
        request-delete-todo-error (re-frame/subscribe [::subs/request-delete-todo-error])]
    [:div
     [:div
      [:h1 {:class "page-title"} (str "Todo List")]
      (when @loading "Loading...")
      (when @request-delete-todo-error
        [:div {:class "notification is-danger"}
         [:h1 (str "Error Delete Todo")]
         [:button {:class "delete" :on-click #(re-frame/dispatch [::events/clear-request-delete-todo-error])} "Done"]])
      [:table {:class "todos-table"}
       [:thead
        [:tr [:th "Title"] [:th "Status"] [:th {:class "todos-table-set-th"} [fetch-todos-button]]]]
       [:tbody
        (map display-todo @todos)]]]]))

(defn todos-main-component [asd]
  (let [state (reagent/atom {:first_name asd})] ;; you can include state
    (reagent/create-class
     {:component-did-mount
      (fn []
        (re-frame/dispatch [::events/fetch-todos])
        (println "I am alive.."))

       ;; ... other methods go here
       ;; println state

       ;; name your component for inclusion in error messages
      :display-name "todos-main-component"

       ;; note the keyword for this method
      :reagent-render
      (fn []
        [:div {:class "todos-container"}
         [:div {:class "todos-table-container"}
          [:div
           [new-todo-form]]
          [todo-list]]])})))



(defn todos-index []
  [:div   
   [:div
    [todos-main-component]]])


(defmethod routes/panels :todos-index-panel [] [todos-index])
