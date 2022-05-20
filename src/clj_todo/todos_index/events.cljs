(ns clj-todo.todos-index.events
    (:require
     [re-frame.core :as re-frame]
     [clj-todo.db :as db]
     [day8.re-frame.http-fx]
     [ajax.core :as ajax]
     [day8.re-frame.tracing :refer-macros [fn-traced]]
     ))


(re-frame/reg-event-fx
    ::fetch-todos
    (fn [{:keys [db]} _]
        {:db (assoc db :loading true)
        :http-xhrio {:method          :get
                    :uri             "https://my-json-server.typicode.com/yvzkr/todo-json/todos"
                    :timeout         8000                                           ;; optional see API docs
                    :response-format (ajax/json-response-format {:keywords? true})  ;; IMPORTANT!: You must provide this.
                    :on-success      [::fetch-todos-success]
                    :on-failure      [:bad-http-result]}}))



(re-frame/reg-event-db
    ::fetch-todos-success
    (fn [db [_ data]]
        (-> db 
            (assoc :loading false)
            (assoc :todos data)
        )
    )
)


;;form event
(re-frame/reg-event-db
    ::update-form
    (fn [db [_ id val]]
        (assoc-in db [:form id ] val )))

(re-frame/reg-event-db
    ::save-todo-form
    (fn [db]
        (let [form-data (:form db)
            todos (get db :todos [])
            updated-todos (conj todos { :id (+ 1 (count todos)) :title (:title form-data) })]
            (-> db
                (assoc :todos updated-todos); ata
                (dissoc :form )             ; sil
            )
        )
    )
)



