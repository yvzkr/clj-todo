(ns clj-todo.events
  (:require
   [re-frame.core :as re-frame]
   [clj-todo.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-fx
  ::navigate
  (fn-traced [_ [_ handler]]
   {:navigate handler}))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn-traced [{:keys [db]} [_ active-panel]]
   {:db (assoc db :active-panel active-panel)}))

(re-frame/reg-event-fx
  ::set-route
  (fn-traced [{:keys [db]} [_ route]]
    {:db (assoc db :route route)}))




(re-frame/reg-event-db
  ::update-name
  (fn [db [_ val]] ; val arg
    (assoc db :name val)))


(re-frame/reg-event-db
  ::update-api-url-text
  (fn [db [_ val]] ; val arg
    (assoc db :new-api-url val)))


(re-frame/reg-event-db
  ::load-new-api-url-to-api-url
  (fn [db]
    (-> db
        (assoc :api-url (:new-api-url db))
        (assoc :success-update-api-url true))))


(re-frame/reg-event-db
    ::load-api-url-form
    (fn [db]
      (assoc db :new-api-url (:api-url db))))

(re-frame/reg-event-db
    ::clear-success-update-api-url
    (fn [db]
        (assoc db :success-update-api-url false)))