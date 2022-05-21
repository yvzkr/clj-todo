(ns clj-todo.todo-view.events
    (:require
     [re-frame.core :as re-frame]
     [clj-todo.db :as db]
     [day8.re-frame.http-fx]
     [ajax.core :as ajax]
     [day8.re-frame.tracing :refer-macros [fn-traced]])
    )

;;form event
(re-frame/reg-event-db
    ::update-form
    (fn [db [_ id val]]
        (assoc-in db [:edit-form id ] val )))


(re-frame/reg-event-db
    ::clear-update-todo-error
    (fn [db]
        (assoc db :updated-error false)))


(re-frame/reg-event-db
    ::clear-update-todo-success
    (fn [db]
        (assoc db :request-update-success false)))

(re-frame/reg-event-db
    ::save-todo-form
    (fn [db]
        (let [form-data (:edit-form db)
            todos (get db :todos [])
            updated-todos (conj todos { :id (+ 1 (count todos)) :title (:title form-data) })]
            (-> db
                (assoc :todos updated-todos)            ; ata
                (assoc :success-request-update true)    ; ata
                (dissoc :edit-form )                    ; sil
            )
        )
    )
)

(defn indexes-where
  [pred? coll]
  (keep-indexed #(when (pred? %2) %1) coll))

(defn update-where
  [v pred? f & args]
  (if-let [i (first (indexes-where pred? v))]
    (assoc v i (apply f (v i) args))
    v))

(re-frame/reg-event-db
 ::success-request-update-todo
 (fn [db [_ new_todo]]
   (let [todos (get db :todos [])
         update-todo (update-where todos (fn [todo] (= (:id todo) (:id new_todo))) (fn [todo] new_todo))]
     (-> db
         (assoc :todos update-todo)
         (assoc :request-update-success true)
                ;(dissoc :edit-form)
         ))))


(re-frame/reg-event-db
    ::failure-request-update-todo
    (fn [db [_ result]]
      (let []
        (-> db
            (assoc :updated-error true))))
)


(re-frame/reg-event-fx
    ::request-update-todo
    (fn [{:keys [db]} _]
        (let [form_data (:edit-form db)
              updated-form form_data
              get-url (str "https://my-json-server.typicode.com/yvzkr/todo-json/todos/" (:id form_data) "/" )
              ]
             {:http-xhrio {:method          :put
                           :uri             get-url
                           :params          updated-form
                           :timeout         5000
                           :format          (ajax/json-request-format)
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success      [::success-request-update-todo]
                           :on-failure      [::failure-request-update-todo]}}
        )
    )
)



(re-frame/reg-event-db
    ::load-todo-edit-form
    (fn [db [_ val]]
      (assoc db :edit-form val)))


(re-frame/reg-event-db
    ::close-edit-form
    (fn [db [_ ]]
      (dissoc db :edit-form)))


