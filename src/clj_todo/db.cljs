(ns clj-todo.db)

(def default-db
  {:name "re-frame"
   :todos [
     {:id 1 :title "Work" :remark "work about bills"}
     {:id 2 :title "Play Soccer" :remark "Play with friend"}]
  }
)
