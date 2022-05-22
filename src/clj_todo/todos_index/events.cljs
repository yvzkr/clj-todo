(ns clj-todo.todos-index.events
    (:require
     [re-frame.core :as re-frame]
     [clj-todo.db :as db]
     [day8.re-frame.http-fx]
     [ajax.core :as ajax]))


(re-frame/reg-event-fx
    ::fetch-todos
    (fn [{:keys [db]} _]
      (let [api-url (:api-url db)]{:db (assoc db :loading true)
        :http-xhrio {:method          :get
                    :uri             api-url
                    :timeout         8000
                    :response-format (ajax/json-response-format {:keywords? true})
                    :on-success      [::fetch-todos-success]
                    :on-failure      [::failure-request-fetch-todos]}})
        ))

(re-frame/reg-event-db
 ::failure-request-fetch-todos
 (fn [db [_ result]]
   (let []
     (-> db
         (assoc :loading false)
         (assoc :error-request-todos true)))))


(re-frame/reg-event-db
 ::fetch-todos-success
 (fn [db [_ data]]
   (-> db
       (assoc :loading false)
       (assoc :todos data))))


;;form event
(re-frame/reg-event-db
 ::update-form
 (fn [db [_ id val]]
   (assoc-in db [:form id] val)))

(re-frame/reg-event-db
 ::save-todo-form
 (fn [db]
   (let [form-data (:form db)
         todos (get db :todos [])
         updated-todos (conj todos {:id (+ 1 (count todos)) :title (:title form-data)})]
     (-> db
         (assoc :todos updated-todos); ata
         (dissoc :form)             ; sil
         ))))


(re-frame/reg-event-db
 ::success-request-create-todo
 (fn [db [_ result]]
   (let [todos (get db :todos [])
         updated-todos (conj todos result)]
     (-> db
         (assoc :todos updated-todos)
         (assoc :loading false)
         (dissoc :form)))))



(re-frame/reg-event-db
 ::failure-request-create-todo
 (fn [db [_ result]]
   (let []
     (-> db
         (assoc :error-request-create-todo true)))))


(re-frame/reg-event-fx
 ::request-create-todo
 (fn [{:keys [db]} _]
   (let [form_data (:form db)
         api-url (:api-url db)]
     {:http-xhrio {:method          :post
                   :uri             api-url
                   :params          {:title (:title form_data) :status false}
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::success-request-create-todo]
                   :on-failure      [::failure-request-create-todo]}})))



(re-frame/reg-event-db
 ::clear-error-request-create-todo
 (fn [db]
   (dissoc db :error-request-create-todo)))

(re-frame/reg-event-db
 ::clear-error-request-todos
 (fn [db]
   (dissoc db :error-request-todos)))
  



(re-frame/reg-event-fx
 ::request-delete-todo
 (fn [{:keys [db]} [_ val]]
   (let [api-url (:api-url db)
         get-url (str api-url "/" val "/")]
     {:http-xhrio {:method            :delete
                   :uri             get-url
                   :params          {}
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::fetch-todos] ;;success-request-delete-todo val
                   :on-failure      [::failure-request-delete-todo]}})))



(re-frame/reg-event-db
 ::success-request-delete-todo
 (fn [db [_ todo_id]]
   (let [todos (get db :todos [])
              ;; todo_id (:id todo)
         update-todos (remove (fn [todo] (= (:id todo) todo_id)) todos)]
     (-> db
         (assoc :todos update-todos)
         (assoc :loading false)
         (dissoc :form)))))




(re-frame/reg-event-db
 ::failure-request-delete-todo
 (fn [db [_ result]]
   (let []
     (-> db
         (assoc :error-request-delete-todo true)))))


(re-frame/reg-event-db
 ::clear-error-request-delete-todo
 (fn [db]
   (dissoc db :error-request-delete-todo)))

(re-frame/reg-event-db
 ::clear-error-request-change-todo-status
 (fn [db]
   (dissoc db :error-request-change-todo-status)))

(defn indexes-where
  [pred? coll]
  (keep-indexed #(when (pred? %2) %1) coll))

(defn update-where
  [v pred? f & args]
  (if-let [i (first (indexes-where pred? v))]
    (assoc v i (apply f (v i) args))
    v))



(re-frame/reg-event-db
 ::success-request-change-todo-status
 (fn [db [_ result]]
   (let [todos (get db :todos [])
         update-todos (update-where todos (fn [todo] (= (:id todo) (:id result))) (fn [todo] result))]
     (println result)
     (-> db
         (assoc :todos update-todos)
         (assoc :request-update-success true)))))

(re-frame/reg-event-db
 ::failure-request-change-todo-status
 (fn [db [_ result]]
   (let []
     (-> db
         (assoc :error-request-change-todo-status true)))))


(re-frame/reg-event-fx
 ::request-change-todo-status
 (fn [{:keys [db]} [_ val status]]
   (let [api-url (:api-url db)
         get-url (str api-url "/" val "/")]
     {:http-xhrio {:method            :patch
                   :uri             get-url
                   :params          {:status status :id val}
                   :timeout         5000
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [::success-request-change-todo-status]
                   :on-failure      [::failure-request-change-todo-status]}})))



(re-frame/reg-event-db
 ::clear-all-alert-message
 (fn [db]
   (-> db
       (dissoc :error-request-todos)
       (dissoc :error-request-delete-todo)
       (dissoc :error-request-create-todo)
       (dissoc :error-request-change-todo-status))))