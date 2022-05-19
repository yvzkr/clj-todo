(ns clj-todo.todo-view.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.routes :as routes]
              [clj-todo.todo-view.subs :as subs]
              [clj-todo.subs :as route-subs]
              ) 
    )


(defn todo-view []
    (let [route-params @(re-frame/subscribe [::route-subs/route-params])
                  todo @(re-frame/subscribe [::subs/todo (:id route-params)] )
    ]
        [:div (str "The selected todo is " (:title todo))]))


(defmethod routes/panels :todo-view-panel [] [todo-view])
