(ns clj-todo.todos-index.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.todos-index.subs :as subs]
              [clj-todo.routes :as routes]
              [clj-todo.events :as route-events]
              [clj-todo.todos-index.events :as events]
              
              ))




(defn display-todo [{:keys [id title remark] }]
    [:tr {:key id}
        [:td  title]
        [:td  remark]
        [:td  
        [:a { :on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]]) }
        "Edit"]
        " | "
        [:a { :on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]]) }
        "Delete"]
        ]        
    ])

(defn fetch-todos-button []
    [:button {:on-click #(re-frame/dispatch [::events/fetch-todos]) :class "refresh-btn"} "Refresh"]
)





(defn todos-index []
    (let [todos (re-frame/subscribe [::subs/todos] )
        loading (re-frame/subscribe [::subs/loading])
        ]
        [:div {:class "todos-container"}
            [:h1
            (str "Todo List")]
            (when @loading "Loading...")
            [:table {:class "todos-table"}
            [:thead 
            [:tr [:th "Title"] [:th "Description"] [:th {:class "todos-table-set-th"} [fetch-todos-button]]]]
            [:tbody            
            (map display-todo @todos)
            ] ]
        ]
        ))


(defmethod routes/panels :todos-index-panel [] [todos-index])
