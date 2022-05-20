(ns clj-todo.todo-view.subs
    (:require [re-frame.core :as re-frame]))


(re-frame/reg-sub
    ::todo
    (fn [db [_ todo-id]]
      (first (filter (fn [u] (= (:id u) (int todo-id))) (:todos db)))))


(re-frame/reg-sub
    ::form
    (fn [db [_ id]]
        (get-in db [:edit-form id] "")
    )
)

(re-frame/reg-sub
    ::form-is-valid?
    (fn [db [_ form_ids]]
        (every? #(get-in db [:edit-form %]) form_ids)
    )
)


(re-frame/reg-sub
    ::updated-error
    (fn [db]
        (:updated-error db)))


(re-frame/reg-sub
    ::request-update-success
    (fn [db]
        (:request-update-success db)))

