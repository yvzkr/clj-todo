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


(re-frame/reg-event-db
    ::success-request-create-todo
    (fn [db [_ result]]
        (let [ todos (get db :todos [])
               updated-todos (conj todos result)
               ]
            (-> db
                (assoc :todos updated-todos)
                (assoc :loading false)
                (dissoc :form )
            )
        )
    )
)

(re-frame/reg-event-db
    ::failure-request-create-todo
    (fn [db [_ result]]
        (let [ ]
            (-> db
                (assoc :created-error true)
            )
        )
    )
)


(re-frame/reg-event-fx
    ::request-create-todo
    (fn [{:keys [db]} _]
        (let [form_data (:form db)
              updated-form form_data ]
             {:http-xhrio {:method          :post
                           :uri             "https://my-json-server.typicode.com/yvzkr/todo-json/todos"
                           :params          updated-form
                           :timeout         5000
                           :format          (ajax/json-request-format)
                           :response-format (ajax/json-response-format {:keywords? true})
                           :on-success      [::success-request-create-todo]
                           :on-failure      [::failure-request-create-todo]}}
        )
    )
)



(re-frame/reg-event-db
    ::clear-create-todo-error
    (fn [db]
      (assoc db :created-error false)))
  



(re-frame/reg-event-fx
    ::request-delete-todo
    (fn [_world [_ val] ]
        (let [
                get-url (str "https://my-json-server.typicode.com/yvzkr/todo-json/todos/" val "/" )
            ]
            {:http-xhrio {:method            :delete
                            :uri             get-url
                            :params          {}
                            :timeout         5000
                            :format          (ajax/json-request-format)
                            :response-format (ajax/json-response-format {:keywords? true})
                            :on-success      [::success-request-delete-todo val]
                            :on-failure      [::failure-request-delete-todo]}}
        )
    )
)

(re-frame/reg-event-db
    ::success-request-delete-todo
    (fn [db [_ todo_id]]
        (let [todos (get db :todos [])
              ;; todo_id (:id todo)
                update-todos (remove (fn [todo] (= (:id todo) todo_id)) todos)
                ] 
            (-> db
                (assoc :todos update-todos)
                (assoc :loading false)
                (dissoc :form )
            )
        )
    )
)




(re-frame/reg-event-db
    ::failure-request-delete-todo
    (fn [db [_ result]]
        (let [ ]
            (-> db
                (assoc :request-delete-todo-error true)
            )
        )
    )
)


(re-frame/reg-event-db
    ::clear-request-delete-todo-error
    (fn [db]
      (assoc db :request-delete-todo-error false)))