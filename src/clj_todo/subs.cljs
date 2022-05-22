(ns clj-todo.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::active-panel
  (fn [db _]
    (get-in  db [:route :panel])))   ;(:active-panel db)))

(re-frame/reg-sub
  ::route-params
  (fn [db _]
    (get-in  db [:route :route :route-params])))  


(re-frame/reg-sub
 ::api-url
 (fn [db]
   (:api-url db)))

(re-frame/reg-sub
 ::new-api-url
 (fn [db]
   (:new-api-url db)))



(re-frame/reg-sub
 ::success-update-api-url
 (fn [db]
   (:success-update-api-url db)))