(ns clj-todo.todos-index.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.todos-index.subs :as subs]
              [clj-todo.routes :as routes]
              [clj-todo.events :as events]))




(defn display-todo [{:keys [id title remark] }]
    [:tr {:key id}
        [:td  title]
        [:td  remark]
        [:td  
        [:a { :on-click #(re-frame/dispatch [::events/navigate [:todo-view :id id]]) }
        "Edit"]
        " | "
        [:a { :on-click #(re-frame/dispatch [::events/navigate [:todo-view :id id]]) }
        "Delete"]
        ]        
    ])



(defn todos-index []
    (let [todos (re-frame/subscribe [::subs/todos] )]
        [:div
            [:h1
            (str "Todo List")]
            [:table {:class "todos"}
            [:thead 
            [:tr [:th "Title"] [:th "Description"] [:th ""]]]
            [:tbody            
            (map display-todo @todos)
            ] ]               
        ]
        ))


(defmethod routes/panels :todos-index-panel [] [todos-index])
