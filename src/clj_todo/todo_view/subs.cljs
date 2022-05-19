(ns clj-todo.todo-view.subs
    (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
    ::todo
    (fn [db [_ todo-id]]
        (first (filter (fn [u] (= (:id u) (int todo-id))) (:todos db)))))