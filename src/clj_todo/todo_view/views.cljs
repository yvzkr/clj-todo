(ns clj-todo.todo-view.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.routes :as routes]
              [clj-todo.todo-view.subs :as subs]
              [clj-todo.subs :as route-subs]
              [clj-todo.todo-view.events :as events]
              ) 
    )



(defn text-input [id label]
    (let [value (re-frame/subscribe [::subs/form id])]
        [:div.field 
            [:label.label label]
            [:div.control
                [:input.input { :value @value
                                :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])       
                                :type "text" 
                                :placeholder "Text input"}] 
            ]
        ]
    )
)
    
    
(defn textarea-input [id label]
    (let [value (re-frame/subscribe [::subs/form id])]
        [:div.field 
            [:label.label label]
            [:div.control
                [:textarea {:value @value
                            :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])       
                            :class "textarea"
                            :placeholder "Text Input"}]
            ]
        ]
    )
)

(defn edit-todo-form [todo]
    (let [  is-valid? @(re-frame/subscribe [::subs/form-is-valid? [:title]])
            request-update-error (re-frame/subscribe [::subs/updated-error]) ]
        [:div {:class "create-todo-form"}
         [:div
          [:h1 "Todo Detail"]
          [:li
           [:ul "Title :" (:title todo)]
           [:ul "Remark :" (:remark todo)]
           [:ul
            [:a { :on-click #(re-frame/dispatch [::events/load-todo-edit-form todo]) }"Edit Item"]
            ]
           ]
          ]
         (when is-valid?
          [:div
           [:h1 {:class "page-title"} (str "Edit Todo")]
           [text-input :title "Title"]
           [textarea-input :remark "Remark"]
           (when @request-update-error
             [:div {:class "notification is-danger"}
              [:h1 (str "Error Update Todo")]
              [:button {:class "delete" :on-click #(re-frame/dispatch [::events/clear-request-delete-todo-error])} "Done"]])
           [:button.button.is-primary
            {:disabled (not is-valid?)
             :on-click #(re-frame/dispatch [::events/request-update-todo])}
            "Update"]
          [:button.button.is-danger
            {:disabled (not is-valid?)
             :on-click #(re-frame/dispatch [::events/close-edit-form])}
            "Close"]
           ]
           )

         ]
    )
)
    

(defn todo-view []
    (let [route-params @(re-frame/subscribe [::route-subs/route-params])
          todo @(re-frame/subscribe [::subs/todo (:id route-params)])
         
          ]

      [:div (str "The selected todo is " (:title todo))]
      [:div
       [edit-todo-form todo]])
)


(defmethod routes/panels :todo-view-panel [] [todo-view])
