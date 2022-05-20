(ns clj-todo.todos-index.views
    (:require [re-frame.core :as re-frame]
              [clj-todo.todos-index.subs :as subs]
              [clj-todo.routes :as routes]
              [clj-todo.events :as route-events]
              [clj-todo.todos-index.events :as events]
              
              ))




(defn display-todo [{:keys [id title remark] }]
    [:tr {:key id}
        [:td  title]
        [:td  remark]
        [:td  
        [:a { :on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]]) }
        "Edit"]
        " | "
        [:a { :on-click #(re-frame/dispatch [::route-events/navigate [:todo-view :id id]]) }
        "Delete"]
        ]        
    ])

(defn fetch-todos-button []
    [:button {:on-click #(re-frame/dispatch [::events/fetch-todos]) :class "refresh-btn"} "Refresh"]
)

(defn text-input [id label]
    (let [value (re-frame/subscribe [::subs/form id])]
        [:div.field 
            [:label.label label]
            [:div.control
                [:input.input { :value @value
                                :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])       
                                :type "text" 
                                :placeholder "Text input"}] 
            ]
        ]
    )
)


(defn textarea-input [id label]
    (let [value (re-frame/subscribe [::subs/form id])]
        [:div.field 
            [:label.label label]
            [:div.control
                [:textarea {:value @value
                            :on-change #(re-frame/dispatch [::events/update-form id (-> % .-target .-value)])       
                            :class "textarea"
                            :placeholder "Text Input"}]
            ]
        ]
    )        
)




(defn new-todo-form []
    [:div {}
        [:h1 (str "Create New Todo")]
        [text-input :title "Title"]
        [textarea-input :remark "textarea"]

        [:button.button.is-primary 
            { :on-click #(re-frame/dispatch [::events/save-todo-form])}
            "Save"
        ]
    ]
)


;;main todo-index
(defn todos-index []
    (let [todos (re-frame/subscribe [::subs/todos] )
        loading (re-frame/subscribe [::subs/loading])
        ]
        [:div {:class "todos-container"}
            [:div {:class "todos-table-container"}        
                [:h1 (str "Todo List")]
                (when @loading "Loading...")
                [:table {:class "todos-table"}
                    [:thead 
                        [:tr [:th "Title"] [:th "Description"] [:th {:class "todos-table-set-th"} [fetch-todos-button]]]]
                    [:tbody
                        (map display-todo @todos)
                    ]
                ]
            ]
            [:div
                [new-todo-form]
            ]
        ]
    )
)


(defmethod routes/panels :todos-index-panel [] [todos-index])
