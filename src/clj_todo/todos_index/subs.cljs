(ns clj-todo.todos-index.subs
    (:require [re-frame.core :as re-frame]))

;;get all todos from db
;;; return alll todos
    ;; _ for arg
(re-frame/reg-sub
    ::todos
    (fn [db _]
        (:todos db)))


(re-frame/reg-sub
    ::loading
    (fn [db]
        (:loading db)))


(re-frame/reg-sub
    ::form
    (fn [db [_ id]]
        (get-in db [:form id] "")
    )
)

(re-frame/reg-sub
    ::form-is-valid?
    (fn [db [_ form_ids]]
        (every? #(get-in db [:form %]) form_ids)
    )
)

(re-frame/reg-sub
    ::error-request-create-todo
    (fn [db]
        (:error-request-create-todo db)))


(re-frame/reg-sub
    ::error-request-delete-todo
    (fn [db]
        (:error-request-delete-todo db)))


(re-frame/reg-sub
    ::error-request-todos
    (fn [db]
        (:error-request-todos db)))


(re-frame/reg-sub
    ::error-request-change-todo-status
    (fn [db]
        (:error-request-change-todo-status db)))