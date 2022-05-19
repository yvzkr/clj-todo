(ns clj-todo.todo-view.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.routes :as routes]
              [clj-todo.todo-view.subs :as subs]
              ) 
    )


(defn todo-view []
    (let [todo @(re-frame/subscribe [::subs/todo 1])]
        [:div (str "The selected todo is " (:title todo))]))


(defmethod routes/panels :todo-view-panel [] [todo-view])
