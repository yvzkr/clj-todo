(ns clj-todo.todos-index.subs
    (:require [re-frame.core :as re-frame]))

;;get all todos from db
;;; return alll todos
    ;; _ for arg
(re-frame/reg-sub
    ::todos
    (fn [db _]
        (:todos db)))
